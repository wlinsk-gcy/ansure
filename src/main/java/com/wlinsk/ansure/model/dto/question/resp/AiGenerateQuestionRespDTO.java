package com.wlinsk.ansure.model.dto.question.resp;

import com.wlinsk.ansure.model.dto.question.QuestionContentDTO;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: wlinsk
 * @Date: 2024/6/1
 */
@Data
public class AiGenerateQuestionRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3358468409106777405L;
    /**
     * 缓存id题目的id
     */
    private String aiGenerateQuestionId;
    private List<QuestionContentDTO> questionContent;
}
