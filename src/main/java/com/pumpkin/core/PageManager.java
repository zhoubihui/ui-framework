package com.pumpkin.core;

import com.pumpkin.runner.ICaseRunnable;
import com.pumpkin.runner.PageRunner;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

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
        return PageRunner.builder().driver(driver).envConfig(envConfig).build();
    }

    public boolean removeDriver(String caseFileName, ICaseRunnable.Env env) {
        return false;
    }
}
