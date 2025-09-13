package com.wlinsk.ansure.model.dto.app.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@Data
public class DeleteAppReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2735763225201070039L;
    @NotBlank(message = "应用id不可为空")
    private String appId;

}
