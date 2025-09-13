package com.wlinsk.ansure.model.dto.app.req;

import com.wlinsk.ansure.basic.enums.ReviewStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@Data
public class ManagerReviewAddReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -748609130083070020L;
    /**
     * 应用id
     */
    @NotBlank(message = "应用id不能为空")
    private String appId;
    /**
     * 审核状态：0-待审核, 1-通过, 2-拒绝
     */
    @NotNull(message = "审核状态不能为空")
    private ReviewStatusEnum reviewStatus;
    /**
     * 审核信息
     */
    private String reviewMessage;
}
