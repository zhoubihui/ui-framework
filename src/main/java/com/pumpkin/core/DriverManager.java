package com.pumpkin.core;

import com.pumpkin.runner.ICaseRunnable;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.openqa.selenium.WebDriver;

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

    public WebDriver getDriver(String caseFileName, ICaseRunnable.Env env) {
        WebDriver driver = null;
        ICaseRunnable.EnvAndCaps envAndCaps = EnvManager.getInstance().getCaps(caseFileName, env);
        String platformName = envAndCaps.getEnv().getPlatform();
        Platform platform = Arrays.stream(Platform.values()).filter(p -> p.isAlias(platformName)).findFirst().orElse(Platform.APP);
        switch (platform) {
            case APP:
                String targetApp = envAndCaps.getEnv().getTargetApp();
                driver = driverMap.get(initAppKey(platformName, targetApp));
                if (Objects.nonNull(driver))
                    break;
                driver = initAppDriver(null);
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
     * @param caps
     * @return
     */
    private WebDriver initAppDriver(CaseInsensitiveMap<String, Object> caps) {
        return null;
    }
}
