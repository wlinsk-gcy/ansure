package com.wlinsk.ansure.model.dto.user.req;

import jakarta.validation.constraints.Email;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@Data
public class UserRegisterReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 726417202538994018L;

    @Email
    @NotBlank(message = "用户名不能为空")
    private String userAccount;

    @NotBlank(message = "验证码不可为空")
    private String verifyCode;

    @Pattern(regexp = "^[a-zA-Z]\\w{7,15}$",message = "以字母开头，长度在8-16之间，只能包含字母、数字和下划线")
    @NotBlank(message = "密码不能为空")
    private String userPassword;

    @Pattern(regexp = "^[a-zA-Z]\\w{7,15}$",message = "以字母开头，长度在8-16之间，只能包含字母、数字和下划线")
    @NotBlank(message = "确认密码不能为空")
    private String checkPassword;
}
