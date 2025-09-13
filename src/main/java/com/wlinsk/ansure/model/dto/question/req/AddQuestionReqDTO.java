package com.wlinsk.ansure.model.dto.question.req;

import com.wlinsk.ansure.model.dto.question.QuestionContentDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: wlinsk
 * @Date: 2024/5/28
 */
@Data
public class AddQuestionReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6755660818793456437L;

    @NotBlank(message = "应用id不可为空")
    private String appId;

    @Valid
    @NotEmpty(message = "题目内容不可为空")
    private List<QuestionContentDTO> questionContent;

}
