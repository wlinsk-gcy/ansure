package com.wlinsk.ansure.basic.handler;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author: wlinsk
 * @Date: 2024/4/21
 */
public enum CodeType {
    Integer {
        public Object getObject(ResultSet rs, String columnName) throws SQLException {
            return rs.getInt(columnName);
        }

        public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getInt(columnIndex);
        }

        public Object getObject(CallableStatement cs, int columnIndex) throws SQLException {
            return cs.getObject(columnIndex);
        }
    },
    String {
        public Object getObject(ResultSet rs, String columnName) throws SQLException {
            return rs.getString(columnName);
        }

        public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getString(columnIndex);
        }

        public Object getObject(CallableStatement cs, int columnIndex) throws SQLException {
            return cs.getString(columnIndex);
        }
    };

    private CodeType() {
    }

    public abstract Object getObject(ResultSet rs, String columnName) throws SQLException;

    public abstract Object getObject(ResultSet rs, int columnIndex) throws SQLException;

    public abstract Object getObject(CallableStatement cs, int columnIndex) throws SQLException;
}