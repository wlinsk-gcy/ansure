package com.wlinsk.ansure.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wlinsk.ansure.basic.enums.UserRoleEnum;
import com.wlinsk.ansure.basic.handler.UniversalEnumHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;

/**
 * 用户表
 * @TableName tb_user
 */
@TableName(value ="tb_user",autoResultMap = true)
@Data
public class User extends BaseEntity implements Serializable {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 微信开放平台id
     */
    private String unionId;

    /**
     * 公众号openId
     */
    private String mpOpenId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * AI点数
     */
    private Integer aiPoint;

    /**
     * 用户角色：user/admin/ban
     */
    @TableField(typeHandler = UniversalEnumHandler.class,jdbcType = JdbcType.VARCHAR)
    private UserRoleEnum userRole;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}