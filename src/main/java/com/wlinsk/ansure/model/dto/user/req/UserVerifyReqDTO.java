package com.wlinsk.ansure.model.dto.user.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author wlinsk
 * @date 2025/9/13
 */
@Data
public class UserVerifyReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1192834724319714876L;

    @Email(message = "请输入有效的邮箱")
    @NotBlank(message = "用户名不能为空")
    private String userAccount;
}

