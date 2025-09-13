package com.wlinsk.ansure.basic.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wlinsk.ansure.basic.serializer.JackSonDeserializer;
import com.wlinsk.ansure.basic.serializer.JackSonSerializer;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@JsonSerialize(using = JackSonSerializer.class)
@JsonDeserialize(using = JackSonDeserializer.class)
public enum ReviewStatusEnum implements BaseEnum<ReviewStatusEnum,Integer>{
    TO_BE_REVIEWED(0,"待审核"),
    REVIEW_PASS(1,"审核通过"),
    REVIEW_FAIL(2,"审核不通过"),
    ;

    private final Integer code;

    private final String message;

    ReviewStatusEnum(Integer code, String message) {
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
