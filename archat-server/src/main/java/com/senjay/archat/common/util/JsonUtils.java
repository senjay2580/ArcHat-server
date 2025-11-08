package com.senjay.archat.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JSON工具类
 * 提供JSON序列化和反序列化功能
 */
@Slf4j
public class JsonUtils {
    
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    /**
     * 将JSON字符串转换为指定类型的对象
     */
    public static <T> T toObject(String jsonString, Class<T> clazz) {
        try {
            return JSON_MAPPER.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON转换对象失败: {}", e.getMessage());
            throw new IllegalArgumentException("JSON格式错误", e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的对象（支持泛型）
     */
    public static <T> T toObject(String jsonString, TypeReference<T> typeReference) {
        try {
            return JSON_MAPPER.readValue(jsonString, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON转换对象失败: {}", e.getMessage());
            throw new IllegalArgumentException("JSON格式错误", e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的列表
     */
    public static <T> List<T> toList(String jsonString, Class<T> clazz) {
        try {
            return JSON_MAPPER.readValue(jsonString, 
                JSON_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            log.error("JSON转换列表失败: {}", e.getMessage());
            throw new IllegalArgumentException("JSON格式错误", e);
        }
    }

    /**
     * 将JSON字符串转换为JsonNode
     */
    public static JsonNode toJsonNode(String jsonString) {
        try {
            return JSON_MAPPER.readTree(jsonString);
        } catch (JsonProcessingException e) {
            log.error("JSON解析失败: {}", e.getMessage());
            throw new IllegalArgumentException("JSON格式错误", e);
        }
    }

    /**
     * 将JsonNode转换为指定类型的对象
     */
    public static <T> T nodeToValue(JsonNode node, Class<T> clazz) {
        try {
            return JSON_MAPPER.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            log.error("JsonNode转换对象失败: {}", e.getMessage());
            throw new IllegalArgumentException("转换失败", e);
        }
    }

    /**
     * 将对象转换为JSON字符串
     */
    public static String toString(Object object) {
        try {
            return JSON_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("对象转换JSON失败: {}", e.getMessage());
            throw new IllegalArgumentException("序列化失败", e);
        }
    }

    private JsonUtils() {
        throw new UnsupportedOperationException("工具类不能实例化");
    }
}
