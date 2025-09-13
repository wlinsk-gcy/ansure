package com.wlinsk.ansure.basic.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wlinsk.ansure.basic.serializer.JackSonDeserializer;
import com.wlinsk.ansure.basic.serializer.JackSonSerializer;

/**
 * @Author: wlinsk
 * @Date: 2024/4/21
 */
@JsonSerialize(using = JackSonSerializer.class)
@JsonDeserialize(using = JackSonDeserializer.class)
public enum BooleanEnum implements BaseEnum<BooleanEnum,Integer> {

    NO(0,"否"),
    YES(1,"是"),
    ;

    private final Integer code;

    private final String message;

    BooleanEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
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
