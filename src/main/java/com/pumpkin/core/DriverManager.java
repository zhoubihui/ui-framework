package com.pumpkin.core;

import com.pumpkin.runner.structure.Env;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.openqa.selenium.WebDriver;

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

    public WebDriver getDriver(String caseFilename, Env env) {
        CaseInsensitiveMap<String, Object> caps = EnvManager.getInstance().getCaps(caseFilename, env);
        String platformName = (String) caps.get(MobileCapabilityType.PLATFORM_NAME);
        switch (platformName) {
            case
        }
    }
}
