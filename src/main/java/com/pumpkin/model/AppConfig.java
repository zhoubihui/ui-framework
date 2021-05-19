package com.pumpkin.model;

import java.util.Map;

/**
 * @className: AppConfig
 * @description: 对应app-config.yaml文件的类结构
 * @author: pumpkin
 * @date: 2021/5/20 7:08 上午
 * @version: 1.0
 **/
public class AppConfig {
    private Map<String, Map<String, Object>> base;
    private Map<String, Map<String, Object>> app;
}
