package com.wlinsk.ansure.service.user.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.wlinsk.ansure.basic.config.ai.SystemPrompt;
import com.wlinsk.ansure.basic.config.redis.RedisUtils;
import com.wlinsk.ansure.basic.config.redisson.RedissonConstant;
import com.wlinsk.ansure.basic.enums.AppTypeEnum;
import com.wlinsk.ansure.basic.enums.ChatEventTypeEnum;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.basic.id_generator.IdUtils;
import com.wlinsk.ansure.basic.transaction.BasicTransactionTemplate;
import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import com.wlinsk.ansure.basic.utils.BusinessValidatorUtils;
import com.wlinsk.ansure.mapper.ChatSessionMapper;
import com.wlinsk.ansure.mapper.QuestionMapper;
import com.wlinsk.ansure.mapper.UserMapper;
import com.wlinsk.ansure.model.constant.Constant;
import com.wlinsk.ansure.model.dto.question.QuestionContentDTO;
import com.wlinsk.ansure.model.dto.question.QuestionContentSSEDTO;
import com.wlinsk.ansure.model.dto.question.req.AiGenerateQuestionReqDTO;
import com.wlinsk.ansure.model.entity.*;
import com.wlinsk.ansure.model.vo.ChatEventVO;
import com.wlinsk.ansure.service.user.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: wlinsk
 * @Date: 2025/7/5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    private final ChatClient simpleChatClient;

    private final RedisUtils redisUtils;

    private final ChatMemory chatMemory;

    private final ChatSessionMapper chatSessionMapper;

    private final UserMapper userMapper;

    private final QuestionMapper questionMapper;

    private final RedissonClient redissonClient;

    private final BasicTransactionTemplate basicTransactionTemplate;

    private final BusinessValidatorUtils businessValidatorUtils;


    @Override
    public Flux<ChatEventVO> chat(String question, String sessionId) {
        String userId = BasicAuthContextUtils.getUserId();
        ChatSession session = chatSessionMapper.queryBySessionId(sessionId);
        String conversationId = ChatService.getConversationId(sessionId);
        try {
            if (Objects.isNull(session) || !session.getUserId().equals(userId)) {
                throw new BasicException(SysCode.DATA_NOT_FOUND);
            }
            if (StringUtils.isBlank(session.getTitle())) {
                int result = chatSessionMapper.updateTitle(question, sessionId);
                if (result != 1) {
                    throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
                }
            }
            int point = queryAiPoint(userId);
            if (point <= 0) {
                throw new BasicException(SysCode.AI_POINT_NOT_ENOUGH);
            }
            reduceAiPoint(userId);
        } catch (BasicException e) {
            log.error("chat error:", e);
            // 当输出被取消时，保存输出的内容到历史记录中
            saveStopHistoryRecordForUser(conversationId, question);
            saveStopHistoryRecord(conversationId, e.getMessage());
            return Flux.just(ChatEventVO.builder()
                    .eventData(e.getMessage())
                    .eventType(ChatEventTypeEnum.ERROR.getValue())
                    .build());
        }
        StringBuilder outputBuilder = new StringBuilder();
        String requestId = IdUtils.build(null);
        ChatEventVO stopEvent = ChatEventVO.builder()
                .eventType(ChatEventTypeEnum.STOP.getValue())
                .build();
        return chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec
                        .text(SystemPrompt.SYSTEM_PROMPT)
                        .param("now", DateUtil.now()))
                .user(question)
                .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
                .toolContext(Map.of(Constant.REQUEST_ID, requestId))
                .stream()
                .chatResponse()
                .doFirst(() -> {
                    redisUtils.setVal(sessionId, "1");
                })
                .doOnComplete(() -> {
                    redisUtils.delVal(sessionId);
                })
                .doOnError(throwable -> redisUtils.delVal(sessionId))
                .doOnCancel(() -> {
                    // 当输出被取消时，保存输出的内容到历史记录中
                    saveStopHistoryRecord(conversationId, outputBuilder.toString());
                })
                //输出过程中判断是否继续输出 -- 用于打断输出
                .takeWhile(chatResponse -> Optional
                        .ofNullable(redisUtils.getVal(sessionId))
                        .orElse("0").equals("1"))
                .map(chatResponse -> {
                    String finishReason = chatResponse.getResult().getMetadata().getFinishReason();
                    if (StrUtil.equals(Constant.STOP, finishReason)) {
                        String messageId = chatResponse.getMetadata().getId();
                        redisUtils.setHashVal(messageId, Constant.REQUEST_ID, requestId);
                    }
                    String text = chatResponse.getResult().getOutput().getText();
                    //同步内容到缓冲区
                    if (StringUtils.isNotBlank(text)) {
                        outputBuilder.append(text);
                    }
                    return ChatEventVO.builder()
                            .eventData(text)
                            .eventType(ChatEventTypeEnum.DATA.getValue())
                            .build();
                })
                .onErrorResume(throwable -> {
                    log.error("ai聊天异常: {}", throwable.getMessage());
                    return Flux.just(ChatEventVO.builder()
                            .eventData(SysCode.AI_SERVICE_ERROR.getMessage())
                            .eventType(ChatEventTypeEnum.ERROR.getValue())
                            .build());
                })
                .concatWith(Flux.defer(() -> {
                    Map<Object, Object> hashStringAll = redisUtils.getHashStringAll(requestId);
                    if (!CollectionUtils.isEmpty(hashStringAll)) {
                        redisUtils.delVal(requestId);
                        for (Object key : hashStringAll.keySet()) {
                            if (key.equals(Constant.ToolsTaskType.QUERY_APP_BY_APP_NAME)) {
                                //拼接题库信息，给前端显示卡片
                                String val = (String) hashStringAll.get(key);
                                List<AppInfo> appInfos = JSON.parseArray(val, AppInfo.class);
                                ChatEventVO build = ChatEventVO.builder().eventData(appInfos)
                                        .eventType(ChatEventTypeEnum.PARAM.getValue())
                                        .build();
                                return Flux.just(build, stopEvent);
                            }
                        }
                    }
                    return Flux.just(stopEvent);
                }));
    }

    @Override
    public void stop(String sessionId) {
        Optional.ofNullable(redisUtils.getVal(sessionId))
                .ifPresent(val -> redisUtils.delVal(sessionId));
    }

    @Override
    public Flux<ChatEventVO> polish(String question) {
        String requestId = IdUtils.build("simple:polish:");
        String userId = BasicAuthContextUtils.getUserId();
        try {
            int point = queryAiPoint(userId);
            if (point <= 0) {
                throw new BasicException(SysCode.AI_POINT_NOT_ENOUGH);
            }
            reduceAiPoint(userId);
        } catch (BasicException e) {
            log.error("polish error:", e);
            return Flux.just(ChatEventVO.builder()
                    .eventData(e.getMessage())
                    .eventType(ChatEventTypeEnum.ERROR.getValue())
                    .build());
        }
        return simpleChatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec
                        .text(SystemPrompt.POLISH_PROMPT))
                .user(question)
                .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, requestId))
                .stream()
                .chatResponse()
                .map(chatResponse -> {
                    String text = chatResponse.getResult().getOutput().getText();
                    return ChatEventVO.builder()
                            .eventData(text)
                            .eventType(ChatEventTypeEnum.DATA.getValue())
                            .build();
                })
                .onErrorResume(throwable -> {
                    log.error("ai润色异常: {}", throwable.getMessage());
                    return Flux.just(ChatEventVO.builder()
                            .eventData(SysCode.AI_POLISH_ERROR.getMessage())
                            .eventType(ChatEventTypeEnum.ERROR.getValue())
                            .build());
                })
                .concatWith(Flux.defer(() -> Flux.just(ChatEventVO.builder()
                        .eventType(ChatEventTypeEnum.STOP.getValue())
                        .build())));
    }

    @Override
    public Flux<ChatEventVO> question(AiGenerateQuestionReqDTO reqDTO) {
        String requestId = IdUtils.build("simple:question:");
        String userId = BasicAuthContextUtils.getUserId();
        App app = null;
        try {
            if (StringUtils.isBlank(reqDTO.getAppId())) {
                throw new BasicException(SysCode.APP_ID_NOT_EXIST);
            }
            if (Objects.isNull(reqDTO.getQuestionNumber())) {
                throw new BasicException(SysCode.QUESTION_NUMBER_NOT_EXIST);
            }
            if (Objects.isNull(reqDTO.getOptionNumber())) {
                throw new BasicException(SysCode.QUESTION_NUMBER_NOT_EXIST);
            }
            log.info("QuestionNumber:{}", reqDTO.getQuestionNumber());
            app = businessValidatorUtils.validateAppExist(reqDTO.getAppId());
            businessValidatorUtils.validateUserInfo(app.getUserId());
            int point = queryAiPoint(userId);
            if (point <= 0) {
                throw new BasicException(SysCode.AI_POINT_NOT_ENOUGH);
            }
            reduceAiPoint(userId);
        } catch (BasicException e) {
            log.error("question error:", e);
            return Flux.just(ChatEventVO.builder()
                    .eventData(e.getMessage())
                    .eventType(ChatEventTypeEnum.ERROR.getValue())
                    .build());
        }
        List<String> oldQuestionTitleList = buildOldQuestionListForAIGenerate(reqDTO.getAppId(), reqDTO.getAiGenerateQuestionId());
        String oldQuestion = String.join(";", oldQuestionTitleList);
        String prompt = getGenerateQuestionUserMessage(app, reqDTO.getQuestionNumber(), reqDTO.getOptionNumber(), oldQuestion);
        log.info("prompt: {}", prompt);
        String aiGenerateQuestionId = StringUtils.isBlank(reqDTO.getAiGenerateQuestionId()) ? IdUtils.build("aiGenerateQuestion") : reqDTO.getAiGenerateQuestionId();
        log.info("aiGenerateQuestionId: {}", aiGenerateQuestionId);
        StringBuilder outputBuilder = new StringBuilder();
        AtomicInteger count = new AtomicInteger(0);
        List<QuestionContentDTO> newQuestionList = new ArrayList<>();
        return simpleChatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(SystemPrompt.GENERATE_QUESTION_SYSTEM_PROMPT))
                .user(prompt)
                .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, requestId))
                .stream()
                .chatResponse()
                .map(chatResponse -> {
                    String text = chatResponse.getResult().getOutput().getText();
                    if (StringUtils.isNotBlank(text)) {
                        for (char c : text.toCharArray()) {
                            if (c == '{') {
                                count.incrementAndGet();
                            }
                            if (count.get() > 0) {
                                outputBuilder.append(c);
                            }
                            if (c == '}') {
                                count.decrementAndGet();
                                if (count.get() == 0) {
                                    String result = outputBuilder.toString();
                                    log.info("ai流式生成题目：{}", result);
                                    QuestionContentDTO dto = JSON.parseObject(result, QuestionContentDTO.class);
                                    newQuestionList.add(dto);
                                    outputBuilder.setLength(0);
                                    // 返回一个事件，携带完整对象
                                    return ChatEventVO.builder()
                                            .eventData(buildQuestionContentSSEDTO(dto, aiGenerateQuestionId))
                                            .eventType(ChatEventTypeEnum.DATA.getValue())
                                            .build();
                                }
                            }
                        }
                    }
                    return ChatEventVO.builder()
                            .eventData(null)
                            .eventType(ChatEventTypeEnum.DATA.getValue())
                            .build();
                })
                .onErrorResume(throwable -> {
                    log.error("生成题目异常: {}", throwable.getMessage());
                    return Flux.just(ChatEventVO.builder()
                            .eventData(SysCode.AI_GENERATE_QUESTION_ERROR.getMessage())
                            .eventType(ChatEventTypeEnum.ERROR.getValue())
                            .build());
                })
                .concatWith(Flux.defer(() -> {
                    cacheQuestionTitle(newQuestionList, oldQuestionTitleList, aiGenerateQuestionId);
                    return Flux.just(ChatEventVO.builder()
                            .eventType(ChatEventTypeEnum.STOP.getValue())
                            .build());
                }));
    }

    private void cacheQuestionTitle(List<QuestionContentDTO> newQuestionList, List<String> oldQuestionTitleList, String aiGenerateQuestionId) {
        if (StringUtils.isBlank(aiGenerateQuestionId)) {
            return;
        }
        List<String> newQuestionTitleList = newQuestionList.stream().map(QuestionContentDTO::getTitle).collect(Collectors.toList());
        newQuestionTitleList.addAll(oldQuestionTitleList);
        redisUtils.setVal(aiGenerateQuestionId, JSON.toJSONString(newQuestionList), 3600);
    }

    private QuestionContentSSEDTO buildQuestionContentSSEDTO(QuestionContentDTO dto, String aiGenerateQuestionId) {
        QuestionContentSSEDTO sseDTO = new QuestionContentSSEDTO();
        sseDTO.setTitle(dto.getTitle());
        sseDTO.setOptions(dto.getOptions());
        sseDTO.setAiGenerateQuestionId(aiGenerateQuestionId);
        log.info("aiGenerateQuestionId: {}", aiGenerateQuestionId);
        return sseDTO;
    }

    private List<QuestionContentDTO> parseQuestionJson(String text) {
        Pattern pattern = Pattern.compile("(\\[.*\\])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            text = matcher.group(1);
            return JSON.parseArray(text, QuestionContentDTO.class);
        }
        return null;
    }

    private void saveStopHistoryRecord(String conversationId, String content) {
        chatMemory.add(conversationId, new AssistantMessage(content));
    }

    private void saveStopHistoryRecordForUser(String conversationId, String content) {
        chatMemory.add(conversationId, new UserMessage(content));
    }

    private void reduceAiPoint(String userId) {
        RLock lock = redissonClient.getLock(RedissonConstant.REDUCE_AI_POINT_LOCK + userId);
        try {
            lock.lock();
            User user = userMapper.queryByUserId(userId);
            Optional.ofNullable(user)
                    .orElseThrow(() -> new BasicException(SysCode.USER_ACCOUNT_NOT_EXIST));
            if (user.getAiPoint() <= 0) {
                throw new BasicException(SysCode.USER_AI_POINT_NOT_ENOUGH);
            }
            basicTransactionTemplate.execute(action -> {
                int result = userMapper.reduceAiPoint(user.getAiPoint(), userId, user.getVersion());
                if (result != 1) {
                    throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
                }
                return SysCode.success;
            });
        } catch (Exception e) {
            log.error("reduceAiPoint error", e);
            if (e instanceof BasicException) {
                throw e;
            }
            throw new BasicException(SysCode.SYSTEM_ERROE);
        } finally {
            if (Objects.nonNull(lock) && lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    private int queryAiPoint(String userId) {
        User user = userMapper.queryByUserId(userId);
        Optional.ofNullable(user)
                .orElseThrow(() -> new BasicException(SysCode.USER_ACCOUNT_NOT_EXIST));
        return user.getAiPoint();
    }

    private List<String> buildOldQuestionListForAIGenerate(String appId, String aiGenerateQuestionId) {
        Question question = questionMapper.queryLatestQuestionContentByAppId(appId);
        List<String> oldQuestionList = new ArrayList<>();
        if (Objects.nonNull(question) && !CollectionUtils.isEmpty(question.getQuestionContent())) {
            oldQuestionList = question.getQuestionContent().stream().map(QuestionContentDTO::getTitle).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(aiGenerateQuestionId)) {
            String val = redisUtils.getVal(aiGenerateQuestionId);
            if (StringUtils.isNotBlank(val)) {
                oldQuestionList.addAll(JSON.parseArray(val, String.class));
                oldQuestionList = oldQuestionList.stream().distinct().collect(Collectors.toList());
            }
        }
        return oldQuestionList;
    }

    private String getGenerateQuestionUserMessage(App app, int questionNumber, int optionNumber, String oldQuestions) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append("应用名称：");
        userMessage.append(app.getAppName()).append(",\n");
        userMessage.append("应用描述：");
        userMessage.append(app.getAppDesc()).append(",\n");
        userMessage.append("应用类别：");
        userMessage.append(AppTypeEnum.getByCode(app.getAppType().getCode()).getMessage()).append(",\n");
        userMessage.append("要生成的题目数量：");
        userMessage.append(questionNumber).append(",\n");
        userMessage.append("每个题目的选项数：");
        userMessage.append(optionNumber).append(",\n");
        userMessage.append("已存在的题目：").append("\n");
        if (StringUtils.isNotBlank(oldQuestions)) {
            userMessage.append(oldQuestions).append("；\n");
        }
        return userMessage.toString();
    }
}
