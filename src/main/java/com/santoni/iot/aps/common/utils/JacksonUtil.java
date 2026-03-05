package com.santoni.iot.aps.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class JacksonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.findAndRegisterModules();
    }
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Jackson obj toJson error, obj:{}", obj, e);
            return "";
        }
    }

    public static <T> T readAsObj(String s, Class<T> tClass) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        try {
            return MAPPER.readValue(s, tClass);
        } catch (Exception e) {
            log.error("Parse JSON to obj error, json:{}, class:{}", s, tClass, e);
            return null;
        }
    }

    public static <T> List<T> readAsObjList(String s, Class<T> tClass) {
        if (StringUtils.isBlank(s)) {
            return Collections.emptyList();
        }
        TypeFactory typeFactory = MAPPER.getTypeFactory();
        try {
            return MAPPER.readValue(s, typeFactory.constructCollectionType(List.class, tClass));
        } catch (Exception e) {
            log.error("Parse JSON to objList error, json:{}, class:{}", s, tClass, e);
            return Collections.emptyList();
        }
    }

    public static <K, V> Map<K, V> readAsMap(String s, Class<K> kClass, Class<V> vClass) {
        if (StringUtils.isBlank(s)) {
            return Collections.emptyMap();
        }
        TypeFactory typeFactory = MAPPER.getTypeFactory();
        try {
            return MAPPER.readValue(s, typeFactory.constructMapType(Map.class, kClass, vClass));
        } catch (Exception e) {
            log.error("Parse JSON to map error, json:{}, kClass:{}, vClass:{}", s, kClass, vClass);
            return Collections.emptyMap();
        }
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
