package com.pumpkin.utils;

import com.google.gson.Gson;

/**
 * @className: JsonUtils
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/30
 * @version: 1.0
 **/
public class JsonUtils {
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
}
