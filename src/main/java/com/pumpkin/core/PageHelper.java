package com.pumpkin.core;

import com.pumpkin.runner.ICaseRunnable;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Map;

/**
 * @className: PageHelper
 * @description: 基本page运行类，封装通用方法
 * @author: pumpkin
 * @date: 2021/5/27 8:44 上午
 * @version: 1.0
 **/
public class PageHelper extends BasePageHelper {

    public PageHelper(WebDriver driver, ICaseRunnable.EnvConfig envConfig, String platformName) {
        super(driver, envConfig, platformName);
    }

    /**
     * 重写父类方法，支持滑动
     * @param by
     * @param multiple
     * @param index
     * @return
     */
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
     * 感觉这样不太好，应该只需要处理findElements和operateElement即可，待优化
     * @param pageFileName
     * @param poStep
     * @param poTrueData
     */
    @Override
    public void runCase(String pageFileName, ICaseRunnable.ElementStructure poStep, Map<String, Object> poTrueData) {
        try {
            super.runCase(pageFileName, poStep, poTrueData);
        } catch (TimeoutException |
                StaleElementReferenceException |
                ArrayIndexOutOfBoundsException |
                NoSuchElementException e) {
            if (envConfig.getConfig().isEnableHandleException() && handleException()) {
                super.runCase(pageFileName, poStep, poTrueData);
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
