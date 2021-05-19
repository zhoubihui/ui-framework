package com.pumpkin.utils;

import java.io.InputStream;

/**
 * @className: FileUtils
 * @description: 文件工具类
 * @author: pumpkin
 * @date: 2021/5/8 7:36 上午
 * @version: 1.0
 **/
public class FileUtils {
    /**
     * 通过加载器，从根目录读取文件
     * 注意：path的第一个字符不能是/
     * @param path
     * @return
     */
    public static InputStream getResourceAsStream(String path) {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(path);
    }
}
