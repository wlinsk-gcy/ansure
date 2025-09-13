package com.wlinsk.ansure.basic.log_interceptor;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/22
 */
@Data
public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 7996578181819127838L;
    private String language;

    private String traceId;
}
