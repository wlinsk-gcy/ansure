package com.wlinsk.ansure.model.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/8/27
 */
@Data
public class TokenBo implements Serializable {
    @Serial
    private static final long serialVersionUID = 6983401602704554140L;
    private String token;
    private String refreshToken;
    private String threePartAccessToken;
    private String threePartRefreshToken;
}
