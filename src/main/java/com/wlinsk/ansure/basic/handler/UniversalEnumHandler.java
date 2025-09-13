package com.wlinsk.ansure.basic.handler;

import com.wlinsk.ansure.basic.enums.BaseEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author: wlinsk
 * @Date: 2024/4/21
 */
@Slf4j
public final class UniversalEnumHandler<T extends BaseEnum<?, ?>> extends BaseTypeHandler<T> {
    private Class<T> type;
    private T[] enums;
    private CodeType codeType;

    public UniversalEnumHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        this.enums = type.getEnumConstants();

        Class<?> clazz = (Class<?>) ((ParameterizedType) getInterfaceBaseEnum(type)).getActualTypeArguments()[1];

        this.codeType = "java.lang.String".equals(clazz.getName()) ? CodeType.String : CodeType.Integer;

        if (this.enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
    }


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (codeType == CodeType.String) {
            ps.setString(i, (String) parameter.getCode());
        } else {
            if (jdbcType == JdbcType.VARCHAR) {
                ps.setString(i, String.valueOf(parameter.getCode()));
            } else {
                ps.setObject(i, parameter.getCode(), jdbcType.TYPE_CODE);
            }
        }
    }

    @Override
    public T getNullableResult(ResultSet resultSet, String s) throws SQLException {
        // 根据数据库存储类型决定获取类型，本例子中数据库中存放String类型
        Object object = codeType.getObject(resultSet, s);
        if (object == null || resultSet.wasNull()) {
            return null;
        }
        return selectEnum(object);
    }

    @Override
    public T getNullableResult(ResultSet resultSet, int i) throws SQLException {
        // 根据数据库存储类型决定获取类型，本例子中数据库中存放String类型
        Object object = codeType.getObject(resultSet, i);
        if (object == null || resultSet.wasNull()) {
            return null;
        }
        return selectEnum(object);
    }

    @Override
    public T getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        // 根据数据库存储类型决定获取类型，本例子中数据库中存放String类型
        Object object = codeType.getObject(callableStatement, i);
        if (object == null || callableStatement.wasNull()) {
            return null;
        }
        return selectEnum(object);
    }

    private Type getInterfaceBaseEnum(Class<T> type) {
        Type[] types = type.getGenericInterfaces();
        for (Type t : types) {
            if (t.getTypeName().contains(BaseEnum.class.getName())) {
                return t;
            }
        }
        throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
    }

    public T selectEnum(Object value) throws SQLException {
        for (T e : enums) {
            if (e.getCode().equals(value)) {
                return e;
            }
        }
        log.error("未知的枚举类型：{},请核对: {}", value, type.getSimpleName());
        throw new SQLException();
    }
}
