package com.wlinsk.ansure.model.dto.user.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/12/22
 */
@Data
public class UpdatePasswordReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1828404152799203930L;

    @NotBlank(message = "原密码不可为空")
    private String originalPassword;
    @Pattern(regexp = "^[a-zA-Z]\\w{7,15}$",message = "以字母开头，长度在8-16之间，只能包含字母、数字和下划线")
    @NotBlank(message = "新密码不可为空")
    private String newPassword;
    @Pattern(regexp = "^[a-zA-Z]\\w{7,15}$",message = "以字母开头，长度在8-16之间，只能包含字母、数字和下划线")
    @NotBlank(message = "确认密码不可为空")
    private String confirmNewPassword;
}
