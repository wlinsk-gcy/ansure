package com.wlinsk.ansure.model.dto.app.req;

import com.wlinsk.ansure.model.dto.IPageReq;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/27
 */
@Data
public class ManagerAppQueryPageReqDTO extends IPageReq implements Serializable {
    @Serial
    private static final long serialVersionUID = 2235385856359178778L;

    private String appId;
    private String appName;
}
