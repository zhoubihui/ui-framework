package com.pumpkin.model;

import com.pumpkin.core.GlobalConfigParse;
import com.pumpkin.utils.ExceptionUtils;
import com.pumpkin.utils.FileUtils;
import com.pumpkin.utils.YamlParse;

import java.io.IOException;

/**
 * @className: Model
 * @description: yaml文件对应的模型父接口
 * @author: pumpkin
 * @date: 2021/5/21 9:08 上午
 * @version: 1.0
 **/
public interface Model {
    /**
     * 根据文件路径获取对应Model子类
     * @param fileName 传入的格式case/search-case.yaml，相对路径
     * @param clazz
     * @param <T>
     * @return
     */
    static <T> T getModel(String fileName, Class<T> clazz) {
        try {
            return YamlParse.readValue(fileName, clazz);
        } catch (IOException e) {
            throw ExceptionUtils.throwAsUncheckedException(e);
        }
    }
}
