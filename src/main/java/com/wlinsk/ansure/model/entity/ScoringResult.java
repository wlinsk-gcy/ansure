package com.wlinsk.ansure.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wlinsk.ansure.basic.handler.ListToJsonHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.util.List;

/**
 * 评分结果表
 * @TableName tb_scoring_result
 */
@TableName(value ="tb_scoring_result",autoResultMap = true)
@Data
public class ScoringResult extends BaseEntity implements Serializable {
    /**
     * 结果id
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
     * 结果图片
     */
    private String resultPicture;

    /**
     * 结果属性集合 JSON，如 [I,S,T,J]
     */
    @TableField(typeHandler = ListToJsonHandler.class,jdbcType = JdbcType.VARCHAR)
    private List<String> resultProp;

    /**
     * 结果得分范围，如 80，表示 80及以上的分数命中此结果
     */
    private Integer resultScoreRange;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户最终得分
     */
    @TableField(exist = false)
    private Integer totalScore;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}