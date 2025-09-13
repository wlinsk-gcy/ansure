package com.wlinsk.ansure.model.dto.app.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2025/7/6
 */
@Data
public class QueryAppListReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2575904686712318564L;
    private String appName;
}
