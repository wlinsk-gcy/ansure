package com.wlinsk.ansure.model.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * @Author: wlinsk
 * @Date: 2024/8/27
 *
 * {
 * "access_token":"4ae82dd9e8134dd5f4a2d597c61ab58e",
 * "token_type":"bearer",
 * "expires_in":86400,
 * "refresh_token":"c3fbadb1a3b27e63d212f217c3b32ace81f7bdc3b0d0e0c49bec946c80c5d5de",
 * "scope":"user_info",
 * "created_at":1724768642
 * }
 */

@Data
public class GiteeLoginCallbackBo implements Serializable {
    @Serial
    private static final long serialVersionUID = 5105475406019052887L;
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String refresh_token;
    private String scope;
    private Integer created_at;
}