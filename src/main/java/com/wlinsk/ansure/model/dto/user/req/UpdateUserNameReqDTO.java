package com.wlinsk.ansure.model.dto.user.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/12/23
 */
@Data
public class UpdateUserNameReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7224012681442514356L;

    @Length(min = 2, max = 20, message = "用户名长度在2到20之间")
    @NotBlank(message = "用户名不能为空")
    private String userName;
}
