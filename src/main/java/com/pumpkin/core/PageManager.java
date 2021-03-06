package com.pumpkin.core;

import com.pumpkin.runner.ICaseRunnable;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.collections4.MapUtils;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @className: PageManager
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/30
 * @version: 1.0
 **/
public class PageManager {
    private static PageManager manager;
    private PageManager() {
    }

    public static PageManager getInstance() {
        synchronized (PageManager.class) {
            if (Objects.isNull(manager))
                manager = new PageManager();
        }
        return manager;
    }

    /**
     * 根据环境配置信息创建PageHelper实例
     * @param caseFileName
     * @param env
     * @return
     */
    public PageHelper getPageHelper(String caseFileName, ICaseRunnable.Env env) {
        ICaseRunnable.EnvConfig envConfig = EnvManager.getInstance().getCaps(caseFileName, env);
        WebDriver driver = DriverManager.getInstance().getDriver(envConfig);
        IPlatform.AppPlatform appPlatform = Arrays.stream(IPlatform.AppPlatform.values()).filter(
                a -> a.isAlias(MapUtils.getString(envConfig.getCaps(), MobileCapabilityType.PLATFORM_NAME))
        ).findFirst().orElseThrow();
        return new PageHelper(driver, envConfig, appPlatform);
    }

    /**
     * 根据环境配置信息移除driver
     * @param caseFileName
     * @param env
     * @param envConfig
     */
    public void removePageHelper(String caseFileName, ICaseRunnable.Env env, ICaseRunnable.EnvConfig envConfig) {
        boolean isRemoveEnv = EnvManager.getInstance().removeEnv(caseFileName, env);
        if (isRemoveEnv) {
            //返回true则去移除driver
            DriverManager.getInstance().removeDriver(envConfig);
        }
    }
}
