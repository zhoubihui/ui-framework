package com.pumpkin.utils;

import java.util.Map;
import java.util.Objects;

/**
 * @className: MapUtils
 * @description: Map处理工具类
 * @author: zhoubihui
 * @date: 2021/5/6 8:54 上午
 * @version: 1.0
 **/
public class MapUtils {
    /**
     * 从map中读取对应类型的value，并根据remove判断是否需要移除
     * @param map
     * @param key
     * @param remove
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getOrRemoveValue(Map<String, Object> map, String key, boolean remove, Class<T> clazz) {
        Object value = remove ? map.remove(key) : map.get(key);
        return clazz.cast(value);
    }
}
