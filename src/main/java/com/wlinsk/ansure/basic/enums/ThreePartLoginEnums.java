package com.wlinsk.ansure.basic.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wlinsk.ansure.basic.serializer.JackSonDeserializer;
import com.wlinsk.ansure.basic.serializer.JackSonSerializer;

/**
 * @Author: wlinsk
 * @Date: 2024/8/26
 */
@JsonSerialize(using = JackSonSerializer.class)
@JsonDeserialize(using = JackSonDeserializer.class)
public enum ThreePartLoginEnums implements BaseEnum<ThreePartLoginEnums,String>{
    GITEE("gitee","gitee"),
    WECHAT("wechat","wechat"),
    ALIPAY("alipay","alipay"),

    ;

    private final String code;
    private final String message;

    ThreePartLoginEnums(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ThreePartLoginEnums getEnumByCode(String code){
        for(ThreePartLoginEnums enums: ThreePartLoginEnums.values()){
            if(enums.getCode().equals(code)){
                return enums;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
