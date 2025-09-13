package com.wlinsk.ansure.model.dto.question.req;

import com.wlinsk.ansure.model.dto.IPageReq;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/28
 */
@Data
public class QueryQuestionPageReqDTO extends IPageReq implements Serializable {
    @Serial
    private static final long serialVersionUID = 514087585116605521L;
    @NotBlank(message = "应用id不可为空")
    private String appId;
}
