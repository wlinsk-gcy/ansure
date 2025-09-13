package com.wlinsk.ansure.basic.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @Author: wlinsk
 * @Date: 2024/4/21
 */
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        //整数取整，小数保留一位; 如果不使用toPlainString(10000=1E+5)会变成科学记数法的型式输出
        gen.writeNumber(value.stripTrailingZeros().toPlainString());
    }
}
