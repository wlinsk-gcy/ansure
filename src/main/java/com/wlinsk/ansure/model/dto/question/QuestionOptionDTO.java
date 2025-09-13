package com.wlinsk.ansure.model.dto.question;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/28
 */
@Data
public class QuestionOptionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -739558462433757454L;
    /**
     * 结果
     */
    private String result;
    /**
     * 得分
     */
    private Integer score;
    /**
     * 选项值：
     */
    @NotBlank(message = "选项值不可为空")
    private String value;
    /**
     * 选项key：A，B，C，D
     */
    @NotBlank(message = "选项key不可为空")
    private String key;
}
