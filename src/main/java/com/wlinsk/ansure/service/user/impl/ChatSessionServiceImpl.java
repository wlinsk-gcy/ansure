package com.wlinsk.ansure.service.user.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wlinsk.ansure.basic.config.SessionProperties;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.id_generator.IdUtils;
import com.wlinsk.ansure.mapper.ChatSessionMapper;
import com.wlinsk.ansure.model.dto.QuerySessionRecordsRespDTO;
import com.wlinsk.ansure.model.entity.ChatSession;
import com.wlinsk.ansure.model.entity.CustomAssistantMessage;
import com.wlinsk.ansure.basic.enums.MessageTypeEnum;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.model.vo.MessageVO;
import com.wlinsk.ansure.model.vo.SessionVO;
import com.wlinsk.ansure.service.user.ChatService;
import com.wlinsk.ansure.service.user.ChatSessionService;
import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements ChatSessionService {

    private final SessionProperties sessionProperties;

    private final ChatSessionMapper chatSessionMapper;
    private final ChatMemory chatMemory;
    // 历史消息数量，默认1000条
    public static final int HISTORY_MESSAGE_COUNT = 1000;

    @Override
    public SessionVO createSession() {
        String userId = BasicAuthContextUtils.getUserId();
        List<ChatSession> sessions = chatSessionMapper.queryTitleRecords(userId);
        SessionVO sessionVO = new SessionVO();
        BeanUtils.copyProperties(sessionProperties, sessionVO);
        sessionVO.setExamples(RandomUtil.randomEleList(sessionProperties.getExamples(), 3));
        if (!CollectionUtils.isEmpty(sessions) && StringUtils.isBlank(sessions.get(0).getTitle())) {
            sessionVO.setSessionId(sessions.get(0).getSessionId());
            int result = chatSessionMapper.updateTime(sessionVO.getSessionId());
            if (result != 1) {
                throw new BasicException(SysCode.SYSTEM_ERROE);
            }
        } else {
            sessionVO.setSessionId(IdUtils.build(null));
            // 构建持久化对象，并持久化
            ChatSession chatSession = ChatSession.builder()
                    .sessionId(sessionVO.getSessionId())
                    .userId(userId)
                    .creater(userId)
                    .updater(userId)
                    .build();
            int save = chatSessionMapper.save(chatSession);
            if (save != 1) {
                throw new BasicException(SysCode.SYSTEM_ERROE);
            }
        }
        return sessionVO;
    }

    @Override
    public List<SessionVO.Example> hotExamples(Integer num) {
        return RandomUtil.randomEleList(sessionProperties.getExamples(), num);
    }

    @Override
    public List<MessageVO> queryBySessionId(String sessionId) {
        String conversationId = ChatService.getConversationId(sessionId);
        List<Message> messages = chatMemory.get(conversationId, HISTORY_MESSAGE_COUNT);
        return messages
                .stream()
                .filter(message -> Objects.nonNull(message)
                        && (message.getMessageType() == MessageType.ASSISTANT
                        || message.getMessageType() == MessageType.USER))
                .map(message -> {
                    if (message instanceof CustomAssistantMessage) {
                        return MessageVO.builder()
                                .content(message.getText())
                                .type(MessageTypeEnum.valueOf(message.getMessageType().name()))
                                .params(((CustomAssistantMessage) message).getParams())
                                .build();
                    }
                    return MessageVO.builder().content(message.getText())
                            .type(MessageTypeEnum.valueOf(message.getMessageType().name()))
                            .id((Optional.ofNullable(message.getMetadata().get("id"))
                                    .orElse("")).toString())
                            .build();
                })
                .toList();
    }

    @Override
    public QuerySessionRecordsRespDTO queryRecords() {
        String userId = BasicAuthContextUtils.getUserId();
        if (StringUtils.isBlank(userId)) {
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
        List<ChatSession> sessions = chatSessionMapper.queryTitleRecords(userId);
        if (CollectionUtils.isEmpty(sessions)) {
            return new QuerySessionRecordsRespDTO();
        }
        QuerySessionRecordsRespDTO respDTO = new QuerySessionRecordsRespDTO();
        respDTO.setLastSessionId(sessions.get(0).getSessionId());
        List<SessionVO> today = new ArrayList<>();
        List<SessionVO> recentWeek = new ArrayList<>();
        List<SessionVO> recentMonth = new ArrayList<>();
        List<SessionVO> recentYear = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate beforeWeek = LocalDate.now().minusDays(7);
        LocalDate beforeMonth = LocalDate.now().minusDays(30);
        LocalDate beforeYear = LocalDate.now().minusDays(365);
        sessions.forEach(session -> {
            LocalDate localDate = DateUtil.toLocalDateTime(session.getUpdateTime()).toLocalDate();
            if (localDate.equals(now)) {
                today.add(buildSessionVO(session));
            } else if (localDate.isAfter(beforeWeek)) {
                recentWeek.add(buildSessionVO(session));
            } else if (localDate.isAfter(beforeMonth)) {
                recentMonth.add(buildSessionVO(session));
            } else if (localDate.isAfter(beforeYear)) {
                recentYear.add(buildSessionVO(session));
            }
        });
        respDTO.setToday(today);
        respDTO.setRecentWeek(recentWeek);
        respDTO.setRecentMonth(recentMonth);
        respDTO.setRecentYear(recentYear);
        return respDTO;
    }

    private SessionVO buildSessionVO(ChatSession chatSession) {
        return SessionVO.builder()
                .sessionId(chatSession.getSessionId())
                .title(chatSession.getTitle())
                .build();
    }


}




