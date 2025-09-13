package com.wlinsk.ansure.model.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@Data
public class IPageReq implements Serializable {
    @Serial
    private static final long serialVersionUID = -4387737655867948406L;
    /**
     * 页数
     */
    @NotNull(message = "当前页码不可为空")
    private Integer pageNum;
    /**
     * 页面数据条数
     */
    @Max(value = 20,message = "最多每页数据展示20条")
    @NotNull(message = "每页大小不可为空")
    private Integer pageSize;
}
