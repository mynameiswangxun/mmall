package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //取消日期转换为timestamps的默认形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //统一日期字符串格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
        //忽略在json字符串中存在,但是在java对象中不存在对应字段的情况
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static <T> String obj2String(T obj){
        if (obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj:objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse object to String error",e);
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj){
        if (obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj:objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse object to String error",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str,Class<T> tClass){
        if(StringUtils.isEmpty(str) || tClass==null) {
            return null;
        }
        try {
            return tClass.equals(String.class)?(T) str:objectMapper.readValue(str,tClass);
        } catch (IOException e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str,TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str) || typeReference==null) {
            return null;
        }
        try {
            return typeReference.getType().equals(String.class)?(T) str:(T)objectMapper.readValue(str,typeReference);
        } catch (IOException e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str,Class<?> collectionsClass,Class<?>... elementClasses){
        if(StringUtils.isEmpty(str) || collectionsClass==null) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionsClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

}
