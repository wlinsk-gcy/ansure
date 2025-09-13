package com.wlinsk.ansure.model.bo;

import lombok.Data;

/**
 * @Author: wlinsk
 * @Date: 2024/8/26
 */
@Data
public class ThreePathLoginConfigBO {
    //第三方登录提供商
//    public String provider;
    //第三方登录提供商的id
    public String clientId;
    //第三方登录提供商的密钥
    public String clientSecret;
    //第三方登录提供商的回调地址（自己的服务地址）
    public String redirectUrl;
    //第三方登录提供商的授权地址
    public String authorizationUrl;
    //第三方登录提供商的token获取地址
    public String tokenUrl;
    //第三方登录提供商的用户信息获取地址
    public String userInfoUrl;
}
