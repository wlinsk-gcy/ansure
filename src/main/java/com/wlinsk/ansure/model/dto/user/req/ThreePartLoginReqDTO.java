package com.wlinsk.ansure.model.dto.user.req;

import com.wlinsk.ansure.basic.enums.ThreePartLoginEnums;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/8/26
 */
@Data
public class ThreePartLoginReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8435026622142289277L;

    @NotNull(message = "第三方渠道不可为空")
    private ThreePartLoginEnums loginType;

}
