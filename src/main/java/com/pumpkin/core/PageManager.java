package com.pumpkin.core;

import com.pumpkin.runner.ICaseRunnable;
import com.pumpkin.runner.PageRunner;
import org.openqa.selenium.WebDriver;

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

    public PageRunner getPageRunner(String caseFileName, ICaseRunnable.Env env) {
        ICaseRunnable.EnvConfig envConfig = EnvManager.getInstance().getCaps(caseFileName, env);
        WebDriver driver = DriverManager.getInstance().getDriver(envConfig);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return PageRunner.builder().driver(driver).envConfig(envConfig).build();
    }

    public void removeDriver(String caseFileName, ICaseRunnable.Env env, ICaseRunnable.EnvConfig envConfig) {
        boolean isRemoveEnv = EnvManager.getInstance().removeEnv(caseFileName, env);
        if (isRemoveEnv) {
            //返回true则去移除driver
            DriverManager.getInstance().removeDriver(envConfig);
        }
    }
}
