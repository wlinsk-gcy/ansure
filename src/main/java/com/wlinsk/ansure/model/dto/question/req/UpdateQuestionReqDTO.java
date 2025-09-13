package com.wlinsk.ansure.model.dto.question.req;

import com.wlinsk.ansure.model.dto.question.QuestionContentDTO;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: wlinsk
 * @Date: 2024/5/28
 */
@Data
public class UpdateQuestionReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4862840053356475310L;
    @NotBlank(message = "题目id不可为空")
    private String questionId;
    @Valid
    @NotEmpty(message = "题目内容不可为空")
    private List<QuestionContentDTO> questionContent;
}
