package com.wlinsk.ansure.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wlinsk.ansure.model.dto.QuerySessionRecordsRespDTO;
import com.wlinsk.ansure.model.entity.ChatSession;
import com.wlinsk.ansure.model.vo.MessageVO;
import com.wlinsk.ansure.model.vo.SessionVO;

import java.util.List;

/**
 *
 */
public interface ChatSessionService extends IService<ChatSession> {

    SessionVO createSession();

    /**
     * 获取热门会话
     *
     * @return 热门会话列表
     */
    List<SessionVO.Example> hotExamples(Integer num);

    List<MessageVO> queryBySessionId(String sessionId);

    QuerySessionRecordsRespDTO queryRecords();


}
