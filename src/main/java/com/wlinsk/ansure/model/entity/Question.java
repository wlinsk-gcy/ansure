package com.wlinsk.ansure.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wlinsk.ansure.basic.handler.ListToJsonHandler;
import com.wlinsk.ansure.model.dto.question.QuestionContentDTO;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.util.List;

/**
 * 题目表
 * @TableName tb_question
 */
@TableName(value ="tb_question",autoResultMap = true)
@Data
public class Question extends BaseEntity implements Serializable {
    /**
     * 题目id
     */
    private String questionId;

    /**
     * 题目内容（json格式）
     */
    @TableField(typeHandler = ListToJsonHandler.class,jdbcType = JdbcType.VARCHAR)
    private List<QuestionContentDTO> questionContent;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 用户id
     */
    private String userId;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}