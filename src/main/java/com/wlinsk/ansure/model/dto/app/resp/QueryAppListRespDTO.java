package com.wlinsk.ansure.model.dto.app.resp;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wlinsk
 * @Date: 2025/7/6
 */
@Data
public class QueryAppListRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 967823542139263902L;

    private String appId;

    private String appName;

    private String appDesc;

    private String appIcon;

    private String appType;

    private String scoringStrategy;

    private String reviewStatus;

    private Date createTime;
    private Date updateTime;
}
