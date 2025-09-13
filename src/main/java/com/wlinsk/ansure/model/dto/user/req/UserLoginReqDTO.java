package com.wlinsk.ansure.model.dto.user.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@Data
public class UserLoginReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1650140655438129536L;

    @NotBlank(message = "用户名不能为空")
    private String userAccount;

    @Length(min = 8, max = 16, message = "密码长度为8-16位")
    @NotBlank(message = "密码不能为空")
    private String userPassword;
}
