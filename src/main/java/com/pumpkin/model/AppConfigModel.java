package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @className: AppConfig
 * @description: 对应app-config.yaml文件的类结构
 * @author: pumpkin
 * @date: 2021/5/20 7:08 上午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class AppConfigModel {
    private final Logger logger = LoggerFactory.getLogger(AppConfigModel.class);

    private Map<String, Map<String, Object>> base;
    private Map<String, Map<String, Object>> app;
}
