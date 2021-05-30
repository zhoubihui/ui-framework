package com.pumpkin.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;

import java.io.IOException;

/**
 * @className: YamlParse
 * @description: YAML文件处理工具类
 * @author: pumpkin
 * @date: 2021/5/6 8:06 上午
 * @version: 1.0
 **/
public class YamlParse {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    /**
     * 读取数据到指定的结构中
     * @param path
     * @param typeReference
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T readValue(String path, TypeReference<T> typeReference) throws IOException {
        return MAPPER.readValue(FileUtils.getResourceAsStream(path), typeReference);
    }

    /**
     * 读取数据到指定的类中
     * @param path
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T readValue(String path, Class<T> clazz) throws IOException {
        return MAPPER.readValue(FileUtils.getResourceAsStream(path), clazz);
    }
}
