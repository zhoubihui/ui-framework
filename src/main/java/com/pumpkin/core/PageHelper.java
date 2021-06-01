package com.pumpkin.core;

import com.pumpkin.runner.ICaseRunnable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

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
        } catch (IndexOutOfBoundsException e) {
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


}
