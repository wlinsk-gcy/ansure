package com.wlinsk.ansure.model.dto.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/6/2
 */
@Data
public class QuestionContentSSEDTO extends QuestionContentDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1012323221122430989L;
    /**
     * 缓存id题目的id
     */
    private String aiGenerateQuestionId;
}
