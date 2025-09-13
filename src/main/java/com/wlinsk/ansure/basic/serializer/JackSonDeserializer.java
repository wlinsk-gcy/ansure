package com.wlinsk.ansure.basic.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wlinsk.ansure.basic.enums.BaseEnum;
import com.wlinsk.ansure.basic.exception.BasicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * @Author: wlinsk
 * @Date: 2024/4/21
 */
@Slf4j
public class JackSonDeserializer extends JsonDeserializer<BaseEnum> {

    @Override
    public BaseEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            //前端输入的值
            String inputParameter = jsonParser.getText();
            if (StringUtils.isEmpty(inputParameter)) {
                return null;
            }
            JsonStreamContext parsingContext = jsonParser.getParsingContext();
            String currentName = parsingContext.getCurrentName();//字段名
            Object currentValue = parsingContext.getCurrentValue();//需要注入的对象
            Field field = getField(currentValue.getClass(), currentName);
            Class enumClass = field.getType();

            BaseEnum[] values = (BaseEnum[]) enumClass.getEnumConstants();
            BaseEnum baseEnum = null;
            for (BaseEnum value : values) {
                if (inputParameter.equals(value.getCode() + "")) {
                    baseEnum = value;
                    break;
                } else {
                    continue;
                }
            }
            if (baseEnum == null) {
                throw new BasicException("CAS915","INPUT_PARAMETER_EXCEPTION");
            }
            return baseEnum;
        } catch (Exception e) {
            log.error("JsonEnumDeserializer deserialize error: ", e);
            throw new BasicException("CAS914","Enum Deserializer Exception");
        }
    }

    public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            if (clazz == Object.class)
                throw e;
            return getField(clazz.getSuperclass(), name);
        }

    }
}
