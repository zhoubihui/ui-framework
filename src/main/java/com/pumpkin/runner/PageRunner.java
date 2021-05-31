package com.pumpkin.runner;

import com.pumpkin.exception.NotMatchActionException;
import com.pumpkin.utils.ReflectUtils;
import io.appium.java_client.MobileBy;
import io.appium.java_client.remote.MobileCapabilityType;
import lombok.Builder;
import org.apache.commons.collections4.MapUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.pumpkin.utils.ReflectUtils.findMethod;
import static com.pumpkin.utils.ReflectUtils.invokeMethod;

/**
 * @className: PageRunner
 * @description: 基本page运行类，封装通用方法
 * @author: pumpkin
 * @date: 2021/5/27 8:44 上午
 * @version: 1.0
 **/
@Builder
public class PageRunner {
    /**
     * 1、每个case文件运行时都需要初始化PageRunner对象，同时根据环境配置初始化WebDriver
     * 2、PageRunner对象保存着WebDriver对象，后续的操作都是使用这个WebDriver
     */
    private WebDriver driver;
    private ICaseRunnable.EnvConfig envConfig;

    private final static Method SEND_KEYS;
    private final static Method CLICK;
    private final static Method CLEAR;
    private final static Method GET_TEXT;
    private final static Method GET_TEXTS;
    private final static Method GET_ATTRIBUTE;
    static {
        SEND_KEYS = findMethod(PageRunner.class, "sendKeys", WebElement.class, String.class, boolean.class);
        CLICK = findMethod(PageRunner.class, "click", WebElement.class);
        CLEAR = findMethod(PageRunner.class, "clear", WebElement.class);
        GET_TEXT = findMethod(PageRunner.class, "getText", WebElement.class);
        GET_TEXTS = findMethod(PageRunner.class, "getTexts", List.class);
        GET_ATTRIBUTE = findMethod(PageRunner.class, "getAttribute", WebElement.class, String.class);
    }

    public void run(String pageFileName, ICaseRunnable.ElementStructure poStep, Map<String, Object> poTrueData) {
        /**
         * 1、获取对应平台的定位符
         * 2、定位元素：定义如下
         *  multiple=true，说明需要执行driver.findElements()，如果index>=0，说明需要取列表中的某个元素
         *  multiple=false,说明需要执行driver.findElement()
         *  第一个定义存在的问题：基本数据类型int的默认值是0，如果原本想表达不填是全部的，现在发现实现不了，所以将定义一改成：
         *  multiple=true，说明需要执行driver.findElements()，如果index>=0，说明需要取列表中的某个元素
         *  multiple=true，说明需要执行driver.findElements()，如果index<0，说明需要取整个列表
         * 3、操作元素
         *  click:
         *  clear:
         *  sendKeys:
         *  getText:
         *  getAttribute:
         */
        String platformName = MapUtils.getString(envConfig.getCaps(), MobileCapabilityType.PLATFORM_NAME);
        ICaseRunnable.ElementSelector elementSelector = poStep.getSelectors().get(platformName);
        String action = poStep.getAction();
        List<String> data = poStep.getData();

        boolean multiple = elementSelector.isMultiple();
        int index = elementSelector.getIndex();

        List<WebElement> elements = findElements(elementSelector.getStrategy(), elementSelector.getSelector(),
                multiple, index);
        operateElement(pageFileName, elements, action, data, poTrueData, multiple, index);
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
            /**
             * 查找单个元素的两种情况：
             * 1、multiple=false,index=0(默认值)
             * 2、multiple=true,index>=0(输入)
             * 执行findElements(By).get(index),如果没找到元素那么这里会抛出下标越界
             */
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
        Method method = findMethod(pageBy.getByClazz(), pageBy.getFullName(), pageBy.getParameterTypes());
        return (By) ReflectUtils.invokeMethod(method, pageBy.getByClazz(), selector);
    }

    /**
     * 操作元素
     * @param pageFileName page文件名
     * @param elements 需要操作的元素，注意：目前只有action=texts才支持多个元素
     * @param action 动作
     * @param data 这个动作对应的方法需要传入的参数
     * @param poTrueData 实参
     * @param multiple
     * @param index
     * @return
     */
    private Object operateElement(String pageFileName,
                                   List<WebElement> elements,
                                   String action,
                                   List<String> data,
                                   Map<String, Object> poTrueData,
                                   boolean multiple,
                                   int index) {
        PageOperate pageOperate = Arrays.stream(PageOperate.values()).
                filter(p -> p.alias.equalsIgnoreCase(action)).
                findFirst().orElseThrow(() -> new NotMatchActionException(pageFileName, action));
        Method methodOperate = pageOperate.method;
        List<Object> args = new ArrayList<>(methodOperate.getParameterCount());
        /**
         * 参数转换
         */
        if (multiple && index < 0) {
            args.add(elements);
        } else {
            args.add(elements.get(0));
        }
        data.forEach(d -> args.add(poTrueData.get(d)));
        return invokeMethod(methodOperate, this, args.toArray());
    }

    /**
     * 查找元素的最底层封装
     * @param by
     * @param index
     * @return
     */
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
        private final Class<?>[] parameterTypes = new Class[]{String.class};
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

        public Class<?>[] getParameterTypes() {
            return parameterTypes;
        }
    }

    /**
     * 1、记录元素操作的封装方法
     * 3、记录元素操作方法的别名
     */
    enum PageOperate {
        CLEAR(PageRunner.CLEAR, "clear"),
        CLICK(PageRunner.CLICK, "click"),
        SEND_KEYS(PageRunner.SEND_KEYS, "input"),
        GET_TEXT(PageRunner.GET_TEXT,  "text"),
        GET_TEXTS(PageRunner.GET_TEXTS, "texts"),
        GET_ATTRIBUTE(PageRunner.GET_ATTRIBUTE, "attribute"),
        ;
        private final String alias;
        private final Method method;
        PageOperate(Method method, String alias) {
            this.alias = alias;
            this.method = method;
        }
    }
}
