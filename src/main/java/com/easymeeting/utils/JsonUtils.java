package com.easymeeting.utils;

import com.easymeeting.entity.enums.ResponseCodeEnum;
import com.easymeeting.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

/**
 * JSON序列化/反序列化工具类
 * 基于阿里巴巴FastJSON封装，提供对象与JSON字符串的互转、JSON数组转List等核心能力
 * 统一处理转换异常，封装为业务异常并记录日志，适配生产环境的异常标准化处理
 *
 * @author 自定义（可补充）
 * @date 2025-12-25
 */
public class JsonUtils {

    /**
     * 日志对象：记录JSON转换过程中的异常信息，便于生产环境问题排查
     */
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * JSON序列化特性配置：WriteMapNullValue 表示序列化时保留null值的字段（默认FastJSON会忽略null字段）
     * 可扩展添加其他特性，如 SerializerFeature.WriteDateUseDateFormat（日期格式化）等
     */
    public static final SerializerFeature[] FEATURES = new SerializerFeature[]{SerializerFeature.WriteMapNullValue};

    /**
     * 将Java对象序列化为JSON字符串
     * 特性：保留对象中null值的字段，保证JSON字段完整性
     *
     * @param obj 待序列化的Java对象（支持任意可序列化的POJO、Map、List等）
     * @return 序列化后的JSON字符串；若obj为null，返回"null"字符串
     */
    public static String convertObj2Json(Object obj) {
        // 调用FastJSON核心方法，传入自定义序列化特性
        return JSON.toJSONString(obj, FEATURES);
    }

    /**
     * 将JSON字符串反序列化为指定类型的Java对象
     * 异常处理：转换失败时记录错误日志，并抛出业务异常（统一异常码）
     *
     * @param json   待反序列化的JSON字符串（格式需与目标类型匹配）
     * @param classz 目标Java类型的Class对象（如User.class、Order.class）
     * @param <T>    泛型，适配任意目标类型
     * @return 反序列化后的指定类型对象
     * @throws BusinessException 转换异常时抛出，CODE_603通常标识"JSON格式错误/类型不匹配"
     */
    public static <T> T convertJson2Obj(String json, Class<T> classz) {
        try {
            // FastJSON核心方法：将JSON字符串转为指定类型对象
            return JSONObject.parseObject(json, classz);
        } catch (Exception e) {
            // 记录异常日志：包含失败的JSON字符串，便于定位问题
            logger.error("JSON转Java对象失败，待转换JSON：{}", json, e);
            // 封装为业务异常，交由全局异常处理器处理（生产环境规范）
            throw new BusinessException(ResponseCodeEnum.CODE_603);
        }
    }

    /**
     * 将JSON数组字符串反序列化为指定类型的List集合
     * 适用于JSON格式为 [{}, {}, ...] 的场景（如批量查询结果的JSON串）
     *
     * @param json   待反序列化的JSON数组字符串
     * @param classz List中元素的目标类型Class对象（如User.class）
     * @param <T>    泛型，适配任意元素类型
     * @return 反序列化后的List集合；若JSON为空数组，返回空List
     * @throws BusinessException 转换异常时抛出，CODE_603标识"JSON数组转换失败"
     */
    public static <T> List<T> convertJsonArray2List(String json, Class<T> classz) {
        try {
            // FastJSON核心方法：将JSON数组字符串转为指定类型的List
            return JSONArray.parseArray(json, classz);
        } catch (Exception e) {
            // 记录异常日志：包含JSON串和异常栈，便于生产环境排查根因
            logger.error("JSON数组转List失败，待转换JSON：{}", json, e);
            // 统一抛出业务异常，保证异常处理的一致性
            throw new BusinessException(ResponseCodeEnum.CODE_603);
        }
    }
}
