package com.wlinsk.ansure.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wlinsk.ansure.model.Result;
import com.wlinsk.ansure.model.dto.question.req.AddQuestionReqDTO;
import com.wlinsk.ansure.model.dto.question.req.QueryQuestionPageReqDTO;
import com.wlinsk.ansure.model.dto.question.req.UpdateQuestionReqDTO;
import com.wlinsk.ansure.model.dto.question.resp.QueryQuestionPageRespDTO;
import com.wlinsk.ansure.service.user.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用题目相关
 *
 * @Author: wlinsk
 * @Date: 2024/5/28
 */
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 添加题目
     * @param reqDTO
     * @return
     */
    @PostMapping("/add")
    public Result<Void> addQuestion(@Validated @RequestBody AddQuestionReqDTO reqDTO){
        questionService.addQuestion(reqDTO);
        return Result.ok();
    }

    /**
     * 修改题目
     * @param reqDTO
     * @return
     */
    @PostMapping("/update")
    public Result<Void> updateQuestion(@Validated @RequestBody UpdateQuestionReqDTO reqDTO){
        questionService.updateQuestion(reqDTO);
        return Result.ok();
    }

    /**
     * 查询分页
     * @param reqDTO
     * @return
     */
    @PostMapping("/queryPage")
    public Result<IPage<QueryQuestionPageRespDTO>> queryPage(@Validated @RequestBody QueryQuestionPageReqDTO reqDTO){
        IPage<QueryQuestionPageRespDTO> result = questionService.queryPage(reqDTO);
        return Result.ok(result);
    }





}
