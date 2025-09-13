package com.wlinsk.ansure.basic.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wlinsk.ansure.basic.enums.BaseEnum;

import java.io.IOException;

/**
 * @Author: wlinsk
 * @Date: 2024/4/21
 */
public class JackSonSerializer extends JsonSerializer<BaseEnum<?, ?>> {

    @Override
    public void serialize(BaseEnum<?, ?> baseEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Object code = baseEnum.getCode();
        serializerProvider.defaultSerializeValue(code, jsonGenerator);
    }
}
