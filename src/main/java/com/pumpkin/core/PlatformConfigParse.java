package com.pumpkin.core;

import com.pumpkin.model.IConfig;
import com.pumpkin.runner.ICaseRunnable;
import com.pumpkin.utils.ExceptionUtils;
import com.pumpkin.utils.MapUtils;
import com.pumpkin.utils.YamlParse;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * @className: PlatformConfigParse
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/26 4:21 下午
 * @version: 1.0
 **/
public class PlatformConfigParse {
    private final static String APP_CONFIG_PATH = "app-config.yaml";
    private static IConfig.AppConfigModel appConfigModel;
    static {
        try {
            appConfigModel = YamlParse.readValue(APP_CONFIG_PATH, IConfig.AppConfigModel.class);
        } catch (IOException e) {
            ExceptionUtils.throwAsUncheckedException(e);
        }
    }

    public static ICaseRunnable.EnvConfig getConfig(ICaseRunnable.Env env) {
        CaseInsensitiveMap<String, Object> caps = null;
        CaseInsensitiveMap<String, Object> appConfig = null;
        String platformName = env.getPlatform();

        Platform platform = Arrays.stream(Platform.values()).filter(p -> p.isAlias(platformName)).findFirst().
                orElse(Platform.APP);
        switch (platform) {
            case APP:
                String targetApp = env.getTargetApp();
                caps = initAppCaps(platformName, targetApp);
                appConfig = initConfig(platformName, targetApp);
                break;
            case WEB:
                caps = initWeb(platformName);
                break;
        }
        return ICaseRunnable.EnvConfig.builder().caps(caps).config(appConfig).env(env).build();
    }

    /**
     * 获取对应平台下，指定app的caps
     * @param platformName
     * @param targetApp
     * @return
     */
    private static CaseInsensitiveMap<String, Object> initAppCaps(String platformName, String targetApp) {
        CaseInsensitiveMap<String, Object> appCaps = appConfigModel.getAppDetail(platformName, targetApp).getCaps();
        CaseInsensitiveMap<String, Object> globalCaps = appConfigModel.getBase().getCaps();

        CaseInsensitiveMap<String, Object> mergeCaps = MapUtils.mergeMap(globalCaps, appCaps);

        //将platform作为caps的platformName写入caps
        mergeCaps.put(MobileCapabilityType.PLATFORM_NAME, platformName);
        return mergeCaps;
    }

    /**
     * 获取对应平台下，指定app的config
     * @param platformName
     * @param targetApp
     * @return
     */
    private static CaseInsensitiveMap<String, Object> initConfig(String platformName, String targetApp) {
        CaseInsensitiveMap<String, Object> globalConfig = appConfigModel.getBase().getConfig();
        CaseInsensitiveMap<String, Object> appConfig = appConfigModel.getAppDetail(platformName, targetApp).getConfig();
        return MapUtils.mergeMap(globalConfig, appConfig);
    }

    private static CaseInsensitiveMap<String, Object> initWeb(String platform) {
        return null;
    }
}
