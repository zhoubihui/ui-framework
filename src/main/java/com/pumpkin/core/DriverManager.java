package com.pumpkin.core;

import com.pumpkin.exception.NotMatchPlatformException;
import com.pumpkin.runner.ICaseRunnable;
import com.pumpkin.utils.ExceptionUtils;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @className: DriverManager
 * @description: driver管理
 * @author: pumpkin
 * @date: 2021/5/26 10:01 下午
 * @version: 1.0
 **/
public class DriverManager {
    /**
     * key=platform-targetApp
     */
    private static Map<String, WebDriver> driverMap = null;
    private static DriverManager manager;

    private DriverManager() {
        driverMap = new HashMap<>();
    }

    public static DriverManager getInstance() {
        synchronized (DriverManager.class) {
            if (Objects.isNull(manager)) {
                manager = new DriverManager();
            }
        }
        return manager;
    }

    /**
     * 获取driver，有则返回，无则根据caps新建
     * @param envConfig
     * @return
     */
    public WebDriver getDriver(ICaseRunnable.EnvConfig envConfig) {
        WebDriver driver = null;
        String platformName = envConfig.getEnv().getPlatform();
        IPlatform.Platform platform = getPlatform(envConfig);
        switch (platform) {
            case APP:
                String targetApp = envConfig.getEnv().getTargetApp();
                driver = driverMap.get(initAppKey(platformName, targetApp));
                if (Objects.nonNull(driver))
                    break;
                driver = initAppDriver(envConfig);
                driverMap.put(initAppKey(platformName, targetApp), driver);
                break;
            case WEB:
                break;
            default:
        }
        return driver;
    }

    /**
     * 自定义APP平台的driver对应的key
     * @param platform
     * @param targetApp
     * @return
     */
    private String initAppKey(String platform, String targetApp) {
        return platform + "-" + targetApp;
    }

    /**
     * 根据caps创建driver实例
     * @param envConfig
     * @return
     */
    private WebDriver initAppDriver(ICaseRunnable.EnvConfig envConfig) {
        ICaseRunnable.Config config = envConfig.getConfig();
        CaseInsensitiveMap<String, Object> caps = envConfig.getCaps();

        try {
            WebDriver driver = null;
            URL url = new URL(config.getUrl());
            DesiredCapabilities capabilities = new DesiredCapabilities(caps);
            String platformName = MapUtils.getString(caps, MobileCapabilityType.PLATFORM_NAME);
            switch (platformName.toUpperCase()) {
                case "ANDROID":
                    driver = new AndroidDriver<MobileElement>(url, capabilities);
                    break;
                case "IOS":
                    driver = new IOSDriver<MobileElement>(url, capabilities);
                    break;
            }
            return driver;
        } catch (MalformedURLException e) {
            throw ExceptionUtils.throwAsUncheckedException(e);
        }
    }

    /**
     * 根据配置信息执行driver.quit()
     * @param envConfig
     */
    public void removeDriver(ICaseRunnable.EnvConfig envConfig) {
        String platformName = envConfig.getEnv().getPlatform();
        IPlatform.Platform platform = getPlatform(envConfig);
        String key = null;
        switch (platform) {
            case APP:
                key = initAppKey(platformName, envConfig.getEnv().getTargetApp());
                break;
            case WEB:
                break;
            default:
        }
        quit(key);
    }

    /**
     * 如果driver不为空就执行quit
     * @param key
     */
    private void quit(String key) {
        WebDriver driver = driverMap.get(key);
        if (Objects.nonNull(driver))
            driver.quit();
    }

    /**
     * 根据环境配置获取platform平台信息
     * @param envConfig
     * @return
     */
    private IPlatform.Platform getPlatform(ICaseRunnable.EnvConfig envConfig) {
        String platformName = envConfig.getEnv().getPlatform();
        return Arrays.stream(IPlatform.Platform.values()).filter(p -> p.isAlias(platformName)).findFirst().
                orElseThrow(() -> new NotMatchPlatformException(envConfig.toString()));
    }
}
