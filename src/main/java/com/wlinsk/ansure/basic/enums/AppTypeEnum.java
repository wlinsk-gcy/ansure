package com.wlinsk.ansure.basic.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.basic.serializer.JackSonDeserializer;
import com.wlinsk.ansure.basic.serializer.JackSonSerializer;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@JsonSerialize(using = JackSonSerializer.class)
@JsonDeserialize(using = JackSonDeserializer.class)
public enum AppTypeEnum implements BaseEnum<AppTypeEnum,Integer>{
    //（0-得分类，1-测评类）
    SCORE(0,"得分类"),
    TEST(1,"测评类"),
    ;
    private final Integer code;

    private final String message;

    AppTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static AppTypeEnum getByCode(Integer code){
        for (AppTypeEnum appTypeEnum : AppTypeEnum.values()) {
            if(appTypeEnum.getCode().equals(code)){
                return appTypeEnum;
            }
        }
        throw new BasicException(SysCode.ENUM_ERROR);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
