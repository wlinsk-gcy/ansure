package com.wlinsk.ansure.model.dto.user.req;

import com.wlinsk.ansure.basic.enums.UserRoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2025/3/30
 */
@Data
public class ManagerUpdateRoleReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7657989504116070515L;

    @NotBlank(message = "用户id不可为空")
    private String userId;
    @NotNull(message = "请选择一个角色")
    private UserRoleEnum userRole;
}
