package com.wlinsk.ansure.model.dto.user.resp;

import com.wlinsk.ansure.basic.enums.UserRoleEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wlinsk
 * @Date: 2024/5/26
 */
@Data
public class ManagerUserQueryPageRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4944922685304710131L;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 账号
     */
    private String userAccount;

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
     * 用户角色：user/admin/ban
     */
    private UserRoleEnum userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
