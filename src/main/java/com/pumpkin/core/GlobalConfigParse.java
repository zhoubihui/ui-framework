package com.pumpkin.core;

import com.pumpkin.model.config.GlobalBaseModel;
import com.pumpkin.model.config.GlobalConfigModel;
import com.pumpkin.utils.YamlParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * @className: GlobalConfigParse
 * @description: 全局配置文件
 * @author: pumpkin
 * @date: 2021/5/20 8:11 下午
 * @version: 1.0
 **/
public class GlobalConfigParse {
    private final static Logger log = LoggerFactory.getLogger(GlobalConfigParse.class);

    private final static String CONFIG_PATH = "global-config.yaml";
    private static GlobalConfigModel globalConfig;
    static {
        try {
            globalConfig = YamlParse.readValue(CONFIG_PATH, GlobalConfigModel.class);
            //如果base下什么都不写，base=null，所以需要手动初始化GlobalBaseModel
            if (Objects.isNull(globalConfig.getBase()))
                globalConfig.setBase(new GlobalBaseModel());
        } catch (IOException e) {
            log.error("读取{}报错:\n{}", CONFIG_PATH, e);
        }
    }

    public static GlobalConfigModel getGlobalConfig() {
        return globalConfig;
    }
}
