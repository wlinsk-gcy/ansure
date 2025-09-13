package com.wlinsk.ansure.basic.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.wlinsk.ansure.model.dto.question.QuestionContentDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: wlinsk
 * @Date: 2024/4/21
 */
@Slf4j
public class ListToJsonHandler<T extends Object> extends BaseTypeHandler<List<T>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        if(Objects.isNull(parameter)){
            ps.setString(i, null);
        }else {
            ps.setString(i, JSON.toJSONString(parameter));
        }
    }

    /**
     * 明确声明了结果集的列名，会调用该函数；如ResultMap的设置
     * @param rs
     * @param columnName
     * @return
     * @throws SQLException
     */
    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            if (StringUtils.isBlank(rs.getString(columnName))) {
                return null;
            }
            if ("question_content".equals(columnName)){
                return (List<T>) JSON.parseObject(rs.getString(columnName),new TypeReference<List<QuestionContentDTO>>() {
                });
            }
            return JSON.parseObject(rs.getString(columnName),new TypeReference<ArrayList<T>>() {
            });
        } catch (Exception e) {
            log.error("==>ListToJsonHandler getNullableResult转化格式化Json异常", e);
            return null;
        }
    }

    /**
     * 在没有明确声明结果集列名的情况下，会这调用该函数
     * @param rs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            if (StringUtils.isBlank(rs.getString(columnIndex))) {
                return null;
            }
            return JSON.parseObject(rs.getString(columnIndex),new TypeReference<ArrayList<T>>() {
            });
        } catch (Exception e) {
            log.error("==>ListToJsonHandler getNullableResult转化格式化Json异常", e);
            return null;
        }
    }

    /**
     * 供存储过程使用，针对存储过程而设，通过列下标的方式来获取存储过程输出结果中的数据
     * @param cs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            if (StringUtils.isBlank(cs.getString(columnIndex))) {
                return null;
            }
            return JSON.parseObject(cs.getString(columnIndex),new TypeReference<ArrayList<T>>() {
            });
        } catch (Exception e) {
            log.error("==>ListToJsonHandler getNullableResult转化格式化Json异常", e);
            return null;
        }
    }
}
