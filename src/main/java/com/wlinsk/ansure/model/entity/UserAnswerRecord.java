package com.wlinsk.ansure.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wlinsk.ansure.basic.enums.AppTypeEnum;
import com.wlinsk.ansure.basic.enums.ScoringStrategyEnum;
import com.wlinsk.ansure.basic.handler.ListToJsonHandler;
import com.wlinsk.ansure.basic.handler.UniversalEnumHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.util.List;

/**
 * 用户答题记录
 * @TableName tb_user_answer_record
 */
@TableName(value ="tb_user_answer_record",autoResultMap = true)
@Data
public class UserAnswerRecord extends BaseEntity implements Serializable {
    /**
     * 记录id
     */
    private String recordId;

    /**
     * 应用id
     */
    private String appId;

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
     * 用户答案（JSON 数组）
     */
    @TableField(typeHandler = ListToJsonHandler.class,jdbcType = JdbcType.VARCHAR)
    private List<String> choices;

    /**
     * 评分结果id
     */
    private String resultId;

    /**
     * 结果名称，如物流师
     */
    private String resultName;

    /**
     * 结果描述
     */
    private String resultDesc;

    /**
     * 结果图标
     */
    private String resultPicture;

    /**
     * 得分
     */
    private Integer resultScore;

    /**
     * 用户id
     */
    private String userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}