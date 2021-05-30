package com.pumpkin.runner;

import com.pumpkin.utils.ReflectUtils;
import io.appium.java_client.MobileBy;
import io.appium.java_client.remote.MobileCapabilityType;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.openqa.selenium.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.pumpkin.utils.ReflectUtils.findMethod;

/**
 * @className: PageRunner
 * @description: 基本page运行类，封装通用方法
 * @author: pumpkin
 * @date: 2021/5/27 8:44 上午
 * @version: 1.0
 **/
@Data
@Builder
public class PageRunner {
    /**
     * 1、每个case文件运行时都需要初始化PageRunner对象，同时根据环境配置初始化WebDriver
     * 2、PageRunner对象保存着WebDriver对象，后续的操作都是使用这个WebDriver
     */
    private WebDriver driver;
    private ICaseRunnable.EnvConfig envConfig;

    private final static Method FIND_ELEMENT;
    private final static Method FIND_ELEMENTS;
    private final static Method CLICK;
    private final static Method CLEAR;
    private final static Method SEND_KEYS;
    private final static Method GET_TEXT;
    private final static Method GET_TEXTS;
    private final static Method GET_ATTRIBUTE;
    static {
        FIND_ELEMENT = findMethod(PageRunner.class, "findElement", String.class, String.class,
                                    boolean.class, int.class);
        FIND_ELEMENTS = findMethod(PageRunner.class, "findElements", String.class, String.class);
        CLICK = findMethod(PageRunner.class, "click", WebElement.class);
        CLEAR = findMethod(PageRunner.class, "clear", WebElement.class);
        SEND_KEYS = findMethod(PageRunner.class, "sendKeys", WebElement.class, String.class, boolean.class);
        GET_TEXT = findMethod(PageRunner.class, "getText", WebElement.class);
        GET_TEXTS = findMethod(PageRunner.class, "getTexts", List.class);
        GET_ATTRIBUTE = findMethod(PageRunner.class, "getAttribute", WebElement.class, String.class);
    }

    public void run(ICaseRunnable.ElementStructure poStep, Map<String, Object> poTrueData) {
        /**
         * 1、获取对应平台的定位符
         * 2、定位元素：定义如下
         *  multiple=true，说明需要执行driver.findElements()，如果index>=0，说明需要取列表中的某个元素
         *  multiple=false,说明需要执行driver.findElement()
         *  第一个定义存在的问题：基本数据类型int的默认值是0，如果原本想表达不填是全部的，现在发现实现不了，所以将定义一改成：
         *  multiple=true，说明需要执行driver.findElements()，如果index>=0，说明需要取列表中的某个元素
         *  multiple=true，说明需要执行driver.findElements()，如果index<0，说明需要取整个列表
         * 3、操作元素
         */
        String platformName = MapUtils.getString(envConfig.getCaps(), MobileCapabilityType.PLATFORM_NAME);
        ICaseRunnable.ElementSelector selector = poStep.getSelectors().get(platformName);
        if (!selector.isMultiple() || selector.getIndex() >= 0) {

        }

    }

    private List<WebElement> findElements(String strategy, String selector, boolean multiple, int index) {
        List<WebElement> elements = null;
        /**
         * 1、获取By实例: 首先根据别名找到真实的定位符方法名，然后通过反射获取By实例
         * 2、查找元素
         */
        PageBy pageBy = Arrays.stream(PageBy.values()).filter(by -> by.isAlias(strategy)).findFirst().
                orElse(PageBy.ID);
        By by = findBy(pageBy, selector);
        if (multiple && index < 0) {
            //查询全部元素
            elements = findElements0(by);
        } else {
            //查询单个元素
            elements = Collections.singletonList(findElement0(by, index));
        }
        return elements;
    }

    /**
     * 根据反射生成定位符对象
     * @param pageBy
     * @param selector
     * @return
     */
    private By findBy(PageBy pageBy, String selector) {
        Method method = findMethod(pageBy.getByClazz(), pageBy.getFullName(), pageBy.getArgsType());
        return (By) ReflectUtils.invokeMethod(method, pageBy.getByClazz(), selector);
    }

    private WebElement findElement0(By by, int index) {
        return findElements0(by).get(index);
    }

    private List<WebElement> findElements0(By by) {
        return driver.findElements(by);
    }

    /**
     * 操作元素: sendKeys、clear、click
     * @param element
     * @param keyword
     * @param replace
     * @return
     */
    private WebElement sendKeys(WebElement element, String keyword, boolean replace) {
        if (replace)
            clear(element);
        element.sendKeys(keyword);
        return element;
    }

    private WebElement click(WebElement element) {
        element.click();
        return element;
    }

    private WebElement clear(WebElement element) {
        element.clear();
        return element;
    }

    /**
     * 读取元素属性值
     * @param element
     * @return
     */
    private String getText(WebElement element) {
        return element.getText();
    }

    private List<String> getTexts(List<WebElement> elements) {
        return elements.stream().map(this::getText).collect(Collectors.toList());
    }

    private String getAttribute(WebElement element, String name) {
        return element.getAttribute(name);
    }

    private void setTimeOut(long time, TimeUnit unit) {

    }

    private boolean handleException() {
        setTimeOut(0, TimeUnit.SECONDS);
        List<String> blackList = envConfig.getConfig().getBlackList();
        boolean isHandle = blackList.stream().anyMatch(
                by -> {
                    String[] temp = splitSelector(by);
                    List<WebElement> elements = findElements(temp[0], temp[1], true, -1);
                    if (elements.size() > 0) {
                        click(elements.get(0));
                        return true;
                    }
                    return false;
                }
        );
        setTimeOut(10, TimeUnit.SECONDS);
        return isHandle;
    }

    private Object proxyHandleException(Method method, Object... args) {
        try {
            return ReflectUtils.invokeMethod(method, this, args);
        } catch (TimeoutException |
                StaleElementReferenceException |
                ArrayIndexOutOfBoundsException |
                NoSuchElementException e) {
            if (envConfig.getConfig().isEnableHandleException() && handleException()) {
                return ReflectUtils.invokeMethod(method, this, args);
            } else {
                throw e;
            }
        }
    }

    private String[] splitSelector(String selector) {
        return selector.substring(1, selector.length() - 1).split(",");
    }

    /**
     * 1、记录定位方式的别名和全名
     * 2、记录定位方式属于By还是MobileBy
     * 3、记录定位方式的参数类型（其实不写也可以）
     */
    enum PageBy {
        ID(By.class, "id"),
        XPATH(By.class, "xpath"),
        AID(MobileBy.class,"aid", "AccessibilityId"),
        UIAUTOMATOR(MobileBy.class, "uiautomator", "AndroidUIAutomator"),
        ;
        private final Class<? extends By> byClazz;
        private final Class<?>[] argsType = new Class[]{String.class};
        private final String[] aliases;
        PageBy(Class<? extends By> byClazz, String... alias) {
            this.byClazz = byClazz;
            this.aliases = alias;
        }
        public boolean isAlias(String alias) {
            return aliases[0].equalsIgnoreCase(alias);
        }

        public String getFullName() {
            return aliases[aliases.length - 1];
        }

        public Class<? extends By> getByClazz() {
            return byClazz;
        }

        public Class<?>[] getArgsType() {
            return argsType;
        }
    }
}
