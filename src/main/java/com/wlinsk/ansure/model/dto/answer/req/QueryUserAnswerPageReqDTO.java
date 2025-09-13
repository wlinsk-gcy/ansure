package com.wlinsk.ansure.model.dto.answer.req;

import com.wlinsk.ansure.model.dto.IPageReq;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/30
 */
@Data
public class QueryUserAnswerPageReqDTO extends IPageReq implements Serializable {
    @Serial
    private static final long serialVersionUID = -8010096221751272724L;
    private String appId;
    private String recordId;
    private String resultName;
}
