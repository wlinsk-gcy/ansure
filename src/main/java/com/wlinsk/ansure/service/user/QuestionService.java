package com.wlinsk.ansure.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wlinsk.ansure.model.dto.question.req.AddQuestionReqDTO;
import com.wlinsk.ansure.model.dto.question.req.QueryQuestionPageReqDTO;
import com.wlinsk.ansure.model.dto.question.req.UpdateQuestionReqDTO;
import com.wlinsk.ansure.model.dto.question.resp.QueryQuestionPageRespDTO;
import com.wlinsk.ansure.model.entity.Question;

/**
 *
 */
public interface QuestionService extends IService<Question> {

    void addQuestion(AddQuestionReqDTO reqDTO);

    IPage<QueryQuestionPageRespDTO> queryPage(QueryQuestionPageReqDTO reqDTO);

    void updateQuestion(UpdateQuestionReqDTO reqDTO);
}
