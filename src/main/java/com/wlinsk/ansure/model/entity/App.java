package com.wlinsk.ansure.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wlinsk.ansure.basic.enums.AppTypeEnum;
import com.wlinsk.ansure.basic.enums.ReviewStatusEnum;
import com.wlinsk.ansure.basic.enums.ScoringStrategyEnum;
import com.wlinsk.ansure.basic.handler.UniversalEnumHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用表
 * @TableName tb_app
 */
@TableName(value ="tb_app",autoResultMap = true)
@Data
public class App extends BaseEntity implements Serializable {
    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用名
     */
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
    @TableField(typeHandler = UniversalEnumHandler.class,jdbcType = JdbcType.NUMERIC)
    private AppTypeEnum appType;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    @TableField(typeHandler = UniversalEnumHandler.class,jdbcType = JdbcType.NUMERIC)
    private ScoringStrategyEnum scoringStrategy;

    /**
     * 审核状态：0-待审核, 1-通过, 2-拒绝
     */
    @TableField(typeHandler = UniversalEnumHandler.class,jdbcType = JdbcType.NUMERIC)
    private ReviewStatusEnum reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核人 id
     */
    private String reviewerId;

    /**
     * 审核时间
     */
    private Date reviewTime;

    /**
     * 用户id
     */
    private String userId;

    @TableField(exist = false)
    private User userInfo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}