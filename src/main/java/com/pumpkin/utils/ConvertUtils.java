package com.pumpkin.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.google.gson.Gson;

/**
 * @className: ConvertUtils
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/6/1
 * @version: 1.0
 **/
public class ConvertUtils {
    /**
     * 两个类或相同类之间，同名称的字段内容拷贝（深拷贝）
     * @param source
     * @param targetClazz
     * @param <T>
     * @return
     */
    public static <T> T copyObject(Object source, Class<T> targetClazz) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(source), targetClazz);
    }

    public static <T> T convert(Object source, TypeReference<T> typeReference) {
        return Convert.convert(typeReference, source);
    }
}
