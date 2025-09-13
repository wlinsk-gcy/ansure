package com.wlinsk.ansure.model.dto.app.req;


import com.wlinsk.ansure.basic.enums.AppTypeEnum;
import com.wlinsk.ansure.basic.enums.ReviewStatusEnum;
import com.wlinsk.ansure.basic.enums.ScoringStrategyEnum;
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
public class ManagerUpdateAppReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4454267836177371449L;
    /**
     * 应用id
     */
    @NotBlank(message = "应用id不能为空")
    private String appId;

    /**
     * 应用名
     */
    @NotBlank(message = "应用描述不能为空")
    private String appName;

    /**
     * 应用描述
     */
    private String appDesc;

    /**
     * 应用图标
     */
    private String appIcon;

    /**
     * 应用类型（0-得分类，1-测评类）
     */
    @NotNull(message = "应用类别不能为空")
    private AppTypeEnum appType;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    @NotNull(message = "应用评分策略不能为空")
    private ScoringStrategyEnum scoringStrategy;

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
