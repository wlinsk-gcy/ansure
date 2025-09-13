package com.wlinsk.ansure.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wlinsk.ansure.model.dto.answer.req.AddUserAnswerReqDTO;
import com.wlinsk.ansure.model.dto.answer.req.QueryUserAnswerPageReqDTO;
import com.wlinsk.ansure.model.dto.answer.resp.QueryUserAnswerRecordDetailsRespDTO;
import com.wlinsk.ansure.model.entity.UserAnswerRecord;

/**
 *
 */
public interface UserAnswerRecordService extends IService<UserAnswerRecord> {

    String addUserAnswer(AddUserAnswerReqDTO reqDTO);

    QueryUserAnswerRecordDetailsRespDTO queryById(String recordId);

    IPage<QueryUserAnswerRecordDetailsRespDTO> queryPage(QueryUserAnswerPageReqDTO reqDTO);

    void deleteById(String recordId);
}
