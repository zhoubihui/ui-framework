package com.pumpkin.core;

import com.pumpkin.model.config.AppConfigModel;
import com.pumpkin.runner.structure.Env;
import com.pumpkin.utils.YamlParse;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * @className: AppConfigParse
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/26 4:21 下午
 * @version: 1.0
 **/
public class PlatformConfigParse {
    private final static Logger log = LoggerFactory.getLogger(PlatformConfigParse.class);

    private final static String APP_CONFIG_PATH = "app-config.yaml";
    private static AppConfigModel appConfig;
    static {
        try {
            appConfig = YamlParse.readValue(APP_CONFIG_PATH, AppConfigModel.class);
        } catch (IOException e) {
            log.error("读取{}报错:\n{}", APP_CONFIG_PATH, e);
        }
    }

    public static CaseInsensitiveMap<String, Object> getConfig(Env env) {
        CaseInsensitiveMap<String, Object> config = null;
        String platformName = env.getPlatform();

        Platform platform = Arrays.stream(Platform.values()).filter(p -> p.isAlias(platformName)).findFirst().
                orElse(Platform.APP);
        switch (platform) {
            case APP:
                String targetApp = env.getTargetApp();
                config = initAppCaps(platformName, targetApp);
                break;
            case WEB:
                config = initWeb(platformName);
                break;
        }
        return config;
    }

    /**
     * 获取对应平台下，指定app的caps
     * @param platformName
     * @param targetApp
     * @return
     */
    private static CaseInsensitiveMap<String, Object> initAppCaps(String platformName, String targetApp) {
        CaseInsensitiveMap<String, Object> appCaps = appConfig.getAppDetail(platformName, targetApp).getCaps();
        CaseInsensitiveMap<String, Object> globalCaps = appConfig.getBase().getCaps();

        CaseInsensitiveMap<String, Object> mergeCaps = new CaseInsensitiveMap<>();
        mergeCaps.putAll(globalCaps);
        //appCaps会覆盖globalCaps中同名的key
        mergeCaps.putAll(appCaps);

        //将platform作为caps的platformName写入caps
        mergeCaps.put(MobileCapabilityType.PLATFORM_NAME, platformName);
        return mergeCaps;
    }

    private static CaseInsensitiveMap<String, Object> initWeb(String platform) {
        return null;
    }
}
