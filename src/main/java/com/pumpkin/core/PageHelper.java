package com.pumpkin.core;

import com.pumpkin.runner.ICaseRunnable;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @className: PageHelper
 * @description: 基本page运行类，封装通用方法
 * @author: pumpkin
 * @date: 2021/5/27 8:44 上午
 * @version: 1.0
 **/
public class PageHelper extends BasePageHelper {
    private final static Logger logger = LoggerFactory.getLogger(PageHelper.class);

    public PageHelper(WebDriver driver, ICaseRunnable.EnvConfig envConfig, String platformName) {
        super(driver, envConfig, platformName);
    }

    @Override
    protected List<WebElement> findElements(By by, boolean multiple, int index) {
        try {
            return super.findElements(by, multiple, index);
        } catch (ArrayIndexOutOfBoundsException e) {
            /**
             * 滑动后找到元素则再查找一次，否则继续向上抛出异常
             */
            if (envConfig.getConfig().isEnabledScroll() && swipeTo0(by)) {
                return super.findElements(by, multiple, index);
            } else {
                throw e;
            }
        }
    }

    /**
     * 滑动方法，根据平台调用不同平台的滑动功能
     * @param by
     * @return
     */
    private boolean swipeTo0(By by) {
        boolean isScroll = false;
        String platformName = (String) ((AppiumDriver) driver).getCapabilities().
                getCapability(MobileCapabilityType.PLATFORM_NAME);
        switch (platformName.toUpperCase()) {
            case "ANDROID":
                break;
            case "IOS":
                break;
        }
        return isScroll;
    }
}
