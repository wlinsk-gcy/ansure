package com.wlinsk.ansure.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wlinsk.ansure.model.Result;
import com.wlinsk.ansure.model.dto.answer.req.AddUserAnswerReqDTO;
import com.wlinsk.ansure.model.dto.answer.req.QueryUserAnswerPageReqDTO;
import com.wlinsk.ansure.model.dto.answer.resp.QueryUserAnswerRecordDetailsRespDTO;
import com.wlinsk.ansure.service.user.UserAnswerRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户答题相关
 *
 * @Author: wlinsk
 * @Date: 2024/5/30
 */
@RestController
@RequestMapping("/userAnswer")
@RequiredArgsConstructor
public class UserAnswerController {
    private final UserAnswerRecordService userAnswerRecordService;

    /**
     * 答题
     * @param reqDTO
     * @return
     */
    @PostMapping("/add")
    public Result<String> addUserAnswer(@Validated @RequestBody AddUserAnswerReqDTO reqDTO){
        String answerId = userAnswerRecordService.addUserAnswer(reqDTO);
        return Result.ok(answerId);
    }

    /**
     * 根据ID查询答题结果
     * @param recordId
     * @return
     */
    @PostMapping("/queryById/{recordId}")
    public Result<QueryUserAnswerRecordDetailsRespDTO> queryById(@PathVariable("recordId") String recordId){
        QueryUserAnswerRecordDetailsRespDTO result = userAnswerRecordService.queryById(recordId);
        return Result.ok(result);
    }

    /**
     * 分页查询答题结果
     * @param reqDTO
     * @return
     */
    @PostMapping("/queryPage")
    public Result<IPage<QueryUserAnswerRecordDetailsRespDTO>> queryPage(@Validated @RequestBody QueryUserAnswerPageReqDTO reqDTO){
        IPage<QueryUserAnswerRecordDetailsRespDTO> result = userAnswerRecordService.queryPage(reqDTO);
        return Result.ok(result);
    }

    /**
     * 删除答题结果
     * @param recordId
     * @return
     */
    @PostMapping("/deleteById/{recordId}")
    public Result<Void> deleteById(@PathVariable("recordId") String recordId){
        userAnswerRecordService.deleteById(recordId);
        return Result.ok();
    }
}
