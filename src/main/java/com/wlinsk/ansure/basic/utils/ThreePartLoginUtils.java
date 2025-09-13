package com.wlinsk.ansure.basic.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.wlinsk.ansure.basic.enums.ThreePartLoginEnums;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.model.bo.GiteeLoginCallbackBo;
import com.wlinsk.ansure.model.bo.GiteeUserInfoBo;
import com.wlinsk.ansure.model.bo.ThreePathLoginConfigBO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: wlinsk
 * @Date: 2024/8/26
 */
@Component
@ConfigurationProperties(prefix = ThreePartLoginUtils.PREFIX)
@Data
public class ThreePartLoginUtils {
    static final String PREFIX = "wlinsk.three-part-login";

    private Map<String, ThreePathLoginConfigBO> providers;

    public String doAuthorize(ThreePartLoginEnums loginType){
        ThreePathLoginConfigBO configBO = providers.get(loginType.getCode());
        Optional.ofNullable(configBO).orElseThrow(() -> new BasicException(SysCode.NOT_SUPPORT_OPERATION));
        String authorizationUrl = configBO.getAuthorizationUrl();
        String replace = authorizationUrl.replace("{client_id}", configBO.getClientId())
                .replace("{redirect_uri}", configBO.getRedirectUrl());
        String htmlResult = HttpUtil.get(replace);
        //解析目标地址
        String regex = "href=\"(.*?)\"";
        //构建正则表达式
        Pattern pattern = Pattern.compile(regex);
        //将元数据与正则表达式进行匹配
        Matcher matcher = pattern.matcher(htmlResult);
        if (matcher.find()){
            //返回第一个匹配结果
            return matcher.group(1);
        }
        throw new BasicException(SysCode.THREE_PART_LOGIN_ERROR);
    }
    public GiteeLoginCallbackBo getToken(String code, String refreshToken){
        ThreePathLoginConfigBO configBO = providers.get(ThreePartLoginEnums.GITEE.getCode());
        Optional.ofNullable(configBO).orElseThrow(() -> new BasicException(SysCode.NOT_SUPPORT_OPERATION));
        HashMap<String, Object> map = new HashMap<>();
        String tokenUrl = configBO.getTokenUrl();
        String replace = "";
        if (StringUtils.isBlank(refreshToken)){
            replace = tokenUrl.replace("{code}", code)
                    .replace("{client_id}", configBO.getClientId())
                    .replace("&client_secret={client_secret}", "")
                    .replace("{redirect_uri}", configBO.getRedirectUrl());
            map.put("client_secret", configBO.getClientSecret());
        }else {
            int index = tokenUrl.indexOf("?");
            String requestParam = "?grant_type=refresh_token&refresh_token=" + refreshToken;
            replace = tokenUrl.substring(0,index).concat(requestParam);
        }

        String result = HttpUtil.post(replace, map);

        GiteeLoginCallbackBo giteeLoginCallbackBo = JSON.parseObject(result, GiteeLoginCallbackBo.class);
        return giteeLoginCallbackBo;
    }

    public GiteeUserInfoBo getUserInfo(String accessToken){
        ThreePathLoginConfigBO configBO = providers.get(ThreePartLoginEnums.GITEE.getCode());
        Optional.ofNullable(configBO).orElseThrow(() -> new BasicException(SysCode.NOT_SUPPORT_OPERATION));
        String userInfoUrl = configBO.getUserInfoUrl().replace("{access_token}", accessToken);
        String result = HttpUtil.get(userInfoUrl);
        GiteeUserInfoBo giteeUserInfoBo = JSON.parseObject(result, GiteeUserInfoBo.class);
        return giteeUserInfoBo;
    }


}
