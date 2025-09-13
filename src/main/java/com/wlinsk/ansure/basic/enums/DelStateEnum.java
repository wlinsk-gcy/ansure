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
public enum DelStateEnum implements BaseEnum<DelStateEnum, Integer> {

    /**
     * 正常
     */
    NORMAL(10, "正常"),

    /**
     * 软删除
     */
    DEL(11, "软删除"),
    ;

    private Integer code;

    private String message;

    DelStateEnum(Integer code, String message) {
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
