package com.wlinsk.ansure.controller.user;

import com.wlinsk.ansure.model.Result;
import com.wlinsk.ansure.model.dto.QuerySessionRecordsRespDTO;
import com.wlinsk.ansure.model.vo.MessageVO;
import com.wlinsk.ansure.model.vo.SessionVO;
import com.wlinsk.ansure.service.user.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI会话相关
 *
 * @Author: wlinsk
 * @Date: 2025/7/4
 */
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    private final ChatSessionService chatSessionService;

    /**
     * 创建会话
     *
     * @return
     */
    @PostMapping("/create")
    public Result<SessionVO> createSession() {
        SessionVO session = chatSessionService.createSession();
        return Result.ok(session);
    }

    /**
     * 获取推荐示例
     *
     * @param num
     * @return
     */
    @PostMapping("/hot/{num}")
    public Result<List<SessionVO.Example>> hotExamples(@PathVariable("num") Integer num) {
        return Result.ok(chatSessionService.hotExamples(num));
    }

    /**
     * 根据ID查询会话详情
     *
     * @param sessionId
     * @return
     */
    @PostMapping("/{sessionId}")
    public Result<List<MessageVO>> queryBySessionId(@PathVariable("sessionId") String sessionId) {
        return Result.ok(chatSessionService.queryBySessionId(sessionId));
    }

    /**
     * 查询会话列表
     *
     * @return
     */
    @PostMapping("/queryRecords")
    public Result<QuerySessionRecordsRespDTO> queryRecords() {
        QuerySessionRecordsRespDTO result = chatSessionService.queryRecords();
        return Result.ok(result);
    }
}
