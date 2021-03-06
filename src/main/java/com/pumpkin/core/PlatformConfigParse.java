package com.pumpkin.core;

import com.pumpkin.model.IConfig;
import com.pumpkin.runner.ICaseRunnable;
import com.pumpkin.utils.*;
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
        ICaseRunnable.Config appConfig = null;
        String platformName = env.getPlatform();

        IPlatform.Platform platform = Arrays.stream(IPlatform.Platform.values()).filter(p -> p.isAlias(platformName)).findFirst().
                orElse(IPlatform.Platform.APP);
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
        return ICaseRunnable.EnvConfig.builder().platformName(platformName).caps(caps).config(appConfig).env(env).build();
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
    private static ICaseRunnable.Config initConfig(String platformName, String targetApp) {
        IConfig.ConfigModel globalConfig = appConfigModel.getBase().getConfig();
        IConfig.ConfigModel appConfig = appConfigModel.getAppDetail(platformName, targetApp).getConfig();
        IConfig.ConfigModel mergeConfig = ReflectUtils.mergeField(globalConfig, appConfig);
        return ConvertUtils.copyObject(mergeConfig, ICaseRunnable.Config.class);
    }

    private static CaseInsensitiveMap<String, Object> initWeb(String platform) {
        return null;
    }
}
