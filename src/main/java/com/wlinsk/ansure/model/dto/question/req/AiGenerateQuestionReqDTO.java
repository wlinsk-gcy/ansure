package com.wlinsk.ansure.model.dto.question.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/6/1
 */
@Data
public class AiGenerateQuestionReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4417925860270371264L;
    @NotBlank(message = "应用id不可为空")
    private String appId;

    @Min(value = 1, message = "单次生成题目数下限为1")
    @Max(value = 10, message = "单次生成题目数上限为10")
    @NotNull(message = "题目数量不可为空")
    private Integer questionNumber;

    @Min(value = 2, message = "题目选项数下限为2")
    @Max(value = 5, message = "题目选项数上限为5")
    @NotNull(message = "题目选项数不可为空")
    private Integer optionNumber;

    /**
     * 缓存id题目的id
     */
    private String aiGenerateQuestionId;
}
