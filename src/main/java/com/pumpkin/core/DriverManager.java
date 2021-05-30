package com.pumpkin.core;

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

    public WebDriver getDriver(ICaseRunnable.EnvConfig envConfig) {
        WebDriver driver = null;
        String platformName = envConfig.getEnv().getPlatform();
        Platform platform = Arrays.stream(Platform.values()).filter(p -> p.isAlias(platformName)).findFirst().orElse(Platform.APP);
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
}
