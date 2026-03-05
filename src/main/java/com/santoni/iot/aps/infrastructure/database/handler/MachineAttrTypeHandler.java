package com.santoni.iot.aps.infrastructure.database.handler;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@MappedTypes(Map.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MachineAttrTypeHandler extends BaseTypeHandler<Map<String, List<String>>> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int columnIndex, Map<String, List<String>> columnValue, JdbcType jdbcType) throws SQLException {
        var json = toJson(columnValue);
        preparedStatement.setString(columnIndex, json);
    }

    @Override
    public Map<String, List<String>> getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        var json = resultSet.getString(columnName);
        return StringUtils.isBlank(json) ? null : parse(json);
    }

    @Override
    public Map<String, List<String>> getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        var json = resultSet.getString(columnIndex);
        return StringUtils.isBlank(json) ? null : parse(json);
    }

    @Override
    public Map<String, List<String>> getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        var json = callableStatement.getString(columnIndex);
        return StringUtils.isBlank(json) ? null : parse(json);
    }

    private String toJson(Object obj) {
        try {
            return JacksonUtil.getMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, List<String>> parse(String json) {
        try {
            var keyType = TypeFactory.defaultInstance().constructType(String.class);
            var valueType = TypeFactory.defaultInstance().constructCollectionType(List.class, String.class);
            var type = TypeFactory.defaultInstance().constructMapType(Map.class, keyType, valueType);
            return JacksonUtil.getMapper().readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
