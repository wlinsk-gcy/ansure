package com.wlinsk.ansure.controller.user;

import com.wlinsk.ansure.model.Result;
import com.wlinsk.ansure.model.dto.ChatDTO;
import com.wlinsk.ansure.model.dto.question.req.AiGenerateQuestionReqDTO;
import com.wlinsk.ansure.model.vo.ChatEventVO;
import com.wlinsk.ansure.service.user.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI相关
 *
 * @Author: wlinsk
 * @Date: 2025/7/5
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {


    private final ChatService chatService;

    /**
     * 发送消息
     *
     * @param chatDTO
     * @return
     */
    @PostMapping(value = "/do", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatEventVO> chat(@RequestBody ChatDTO chatDTO) {
        return chatService.chat(chatDTO.getQuestion(), chatDTO.getSessionId());
    }

    /**
     * 停止生成
     *
     * @param sessionId
     * @return
     */
    @PostMapping("/stop/{sessionId}")
    public Result<Void> stop(@PathVariable("sessionId") String sessionId) {
        chatService.stop(sessionId);
        return Result.ok();
    }

    /**
     * AI 润色
     *
     * @param chatDTO
     * @return
     */
    @PostMapping(value = "/polish", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatEventVO> polish(@RequestBody ChatDTO chatDTO) {
        return chatService.polish(chatDTO.getQuestion());
    }


    @PostMapping(value = "/question", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatEventVO> question(@Validated @RequestBody AiGenerateQuestionReqDTO reqDTO) {
        return chatService.question(reqDTO);
    }


}
