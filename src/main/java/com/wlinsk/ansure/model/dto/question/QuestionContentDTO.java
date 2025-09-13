package com.wlinsk.ansure.model.dto.question;

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
public class QuestionContentDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -407413857539800612L;
    /**
     * 题目标题
     */
    @NotBlank(message = "题目标题不能为空")
    private String title;

    /**
     * 题目选项列表
     */
    @Valid
    @NotEmpty(message = "题目选项列表不能为空")
    private List<QuestionOptionDTO> options;

}
