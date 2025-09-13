package com.wlinsk.ansure.service.user;

import com.wlinsk.ansure.model.dto.question.req.AiGenerateQuestionReqDTO;
import com.wlinsk.ansure.model.vo.ChatEventVO;
import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import reactor.core.publisher.Flux;

/**
 * @Author: wlinsk
 * @Date: 2025/7/5
 */
public interface ChatService {

    static String getConversationId(String sessionId) {
        return String.format("%s_%s", BasicAuthContextUtils.getUserId(), sessionId);
    }

    Flux<ChatEventVO> chat(String question, String sessionId);

    void stop(String sessionId);

    Flux<ChatEventVO> polish(String question);

    Flux<ChatEventVO> question(AiGenerateQuestionReqDTO reqDTO);
}
