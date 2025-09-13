package com.wlinsk.ansure.model.entity;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: wlinsk
 * @Date: 2025/7/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo {

    @JsonPropertyDescription("应用id")
    private String appId;

    @JsonPropertyDescription("应用名称")
    private String appName;

    @JsonPropertyDescription("应用描述")
    private String appDesc;

    @JsonPropertyDescription("应用图标")
    private String appIcon;

    @JsonPropertyDescription("应用类型：0-得分类，1-测评类")
    private String appType;

    @JsonPropertyDescription("应用评分策略：0-自定义评分，1-AI评分")
    private String scoringStrategy;

    @JsonPropertyDescription("应用审核状态：0-待审核，1-审核通过，2-审核不通过")
    private String reviewStatus;

    @JsonPropertyDescription("应用的创建时间")
    private Date createTime;
    @JsonPropertyDescription("应用的更新时间")
    private Date updateTime;


}
