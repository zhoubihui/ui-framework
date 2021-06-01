package com.pumpkin.core;

import cn.hutool.core.convert.Convert;
import com.pumpkin.exception.NotMatchActionException;
import com.pumpkin.runner.ICaseRunnable;
import com.pumpkin.utils.ConvertUtils;
import com.pumpkin.utils.StringUtils;
import io.appium.java_client.MobileBy;
import lombok.Getter;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.pumpkin.utils.ReflectUtils.*;

/**
 * @className: BasePageHelper
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/31
 * @version: 1.0
 **/
public class BasePageHelper {
    protected WebDriver driver;
    @Getter
    protected ICaseRunnable.EnvConfig envConfig;
    protected String platformName;

    /**
     * 1、每个case文件运行时都需要初始化PageHelper对象，同时根据环境配置初始化WebDriver
     * 2、BasePageHelper对象保存着WebDriver对象，后续的操作都是使用这个WebDriver
     */
    protected BasePageHelper(WebDriver driver, ICaseRunnable.EnvConfig envConfig, String platformName) {
        this.driver = driver;
        this.envConfig = envConfig;
        this.platformName = platformName;
    }

    private final static Method SEND_KEYS;
    private final static Method CLICK;
    private final static Method CLEAR;
    private final static Method GET_TEXT;
    private final static Method GET_TEXTS;
    private final static Method GET_ATTRIBUTE;

    private final static Method FIND_ID;
    private final static Method FIND_XPATH;
    private final static Method FIND_AID;
    private final static Method FIND_UIAUTOMATOR;

    private final static Method FIND_ELEMENTS;
    private final static Method OPERATE_ELEMENT;
    static {
        /**
         * 封装的元素操作
         */
        SEND_KEYS = findMethod(BasePageHelper.class, "sendKeys0", WebElement.class, String.class);
        CLICK = findMethod(BasePageHelper.class, "click0", WebElement.class);
        CLEAR = findMethod(BasePageHelper.class, "clear0", WebElement.class);
        GET_TEXT = findMethod(BasePageHelper.class, "getText0", WebElement.class);
        GET_TEXTS = findMethod(BasePageHelper.class, "getTexts0", List.class);
        GET_ATTRIBUTE = findMethod(BasePageHelper.class, "getAttribute0", WebElement.class, String.class);

        /**
         * 封装的元素定位符
         */
        FIND_ID = findMethod(By.class, "id", String.class);
        FIND_XPATH = findMethod(By.class, "xpath", String.class);
        FIND_AID = findMethod(MobileBy.class, "AccessibilityId", String.class);
        FIND_UIAUTOMATOR = findMethod(MobileBy.class, "AndroidUIAutomator", String.class);

        /**
         * 封装需要做通用异常处理的方法
         */
        FIND_ELEMENTS = findMethod(BasePageHelper.class, "findElements", By.class, boolean.class, int.class);
        OPERATE_ELEMENT = findMethod(BasePageHelper.class, "operateElement",
                String.class, List.class, String.class, List.class, Map.class, boolean.class, int.class
                );
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
     * 未完成: 还可以加上输入后是否按enter键
     * @param element
     * @param keyword
     * @return
     */
    private WebElement sendKeys0(WebElement element, String keyword) {
        if (envConfig.getConfig().isEnabledReplace())
            clear0(element);
        element.sendKeys(keyword);
        return element;
    }

    private WebElement click0(WebElement element) {
        element.click();
        return element;
    }

    private WebElement clear0(WebElement element) {
        element.clear();
        return element;
    }

    /**
     * 读取元素属性值
     * @param element
     * @return
     */
    private String getText0(WebElement element) {
        return element.getText();
    }

    private List<String> getTexts0(List<WebElement> elements) {
        return elements.stream().map(this::getText0).collect(Collectors.toList());
    }

    private String getAttribute0(WebElement element, String name) {
        return element.getAttribute(name);
    }

    private void setTimeOut0(long time, TimeUnit unit) {

    }

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
    public Object runCase(String pageFileName, ICaseRunnable.ElementStructure poStep, Map<String, Object> poTrueData) {
        ICaseRunnable.ElementSelector elementSelector = poStep.getSelectors().get(platformName);
        String action = poStep.getAction();
        List<String> data = poStep.getData();

        boolean multiple = elementSelector.isMultiple();
        int index = elementSelector.getIndex();

        By by = findBy(elementSelector.getStrategy(), elementSelector.getSelector());
        List<WebElement> elements = findElements(by, multiple, index);
        proxyHandleException(FIND_ELEMENTS, by, multiple, index);
        return operateElement(pageFileName, elements, action, data, poTrueData, multiple, index);
    }

    /**
     * 查找元素的统一封装方法
     * @param by
     * @param multiple
     * @param index
     * @return
     */
    protected List<WebElement> findElements(By by, boolean multiple, int index) {
        List<WebElement> elements = null;
        /**
         * 1、获取By实例: 首先根据别名找到真实的定位符方法名，然后通过反射获取By实例
         * 2、查找元素
         *      查找单个元素的两种情况：
         *        1) multiple=false,index=0(默认值)
         *        2) multiple=true,index>=0(输入)
         *        3) 执行findElements(By).get(index),如果没找到元素那么这里会抛出下标越界
         */
        if (multiple && index < 0) {
            elements = findElements0(by);
        } else {
            elements = Collections.singletonList(findElement0(by, index));
        }
        return elements;
    }

    /**
     * 根据反射生成定位符对象
     * @param strategy
     * @param selector
     * @return
     */
    private By findBy(String strategy, String selector) {
        PageBy pageBy = Arrays.stream(PageBy.values()).filter(by -> by.alias.equalsIgnoreCase(strategy)).findFirst().
                orElse(PageBy.ID);
        return (By) invokeMethod(pageBy.method, null, selector);
    }

    /**
     * 操作元素
     * @param pageFileName page文件名
     * @param elements 需要操作的元素，注意：目前只有action=texts才支持多个元素
     * @param action 动作
     * @param data 这个动作对应的方法需要传入的参数
     * @param poTrueData 实参
     * @param multiple 是否使用findElements查找元素
     * @param index 用findElements查找元素后取第几个元素
     * @return 操作方法的返回值
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
         * 1、校验data中的参数是否符合对应action需要的参数(未做)
         * 2、参数转换
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
     * 滑动方法，根据平台调用不同平台的滑动功能
     * @param by
     * @return
     */
    protected boolean swipeTo0(By by) {
        boolean isScroll = false;
        switch (platformName.toUpperCase()) {
            case "ANDROID":
                isScroll = swipeAndroid0(by);
                break;
            case "IOS":
                isScroll = swipeIOS0(by);
                break;
        }
        return isScroll;
    }

    /**
     * Android平台利用UiSelector类来实现滑动
     * @param by
     */
    private boolean swipeAndroid0(By by) {
        String uiSelector = null;
        //获取具体的元素定位方法,拼接到滑动语法中
        if (by.getClass() == MobileBy.ByAndroidUIAutomator.class) {
            //todo 如果原本就是UiSelector的语法
        } else {
            //app中的id是带包名，by.toString()的格式是：ById：com.tencent.wework:id/h8q，所以切割时需要加正则切割
            uiSelector = UiSelectorHelper.transformUiSelector(getInnerClassName(by.getClass()),
                    StringUtils.split(by.toString(), ":\\s{1}")[1]);
            if (uiSelector.isBlank()) {
                return false;
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(");
        builder.append(uiSelector).append(")");
        By uiSelectorBy = findBy("uiautomator", builder.toString());
        findElement0(uiSelectorBy, 0);
        return true;
    }

    /**
     * iOS平台滑动方法
     * @param by
     * @return
     */
    private boolean swipeIOS0(By by) {
        //待定
        return false;
    }

    /**
     * 通用异常处理，enableHandleException=true时会执行此方法
     * @return
     */
    private boolean handleException() {
        setTimeOut0(0, TimeUnit.SECONDS);
        List<String> blackList = envConfig.getConfig().getBlackList();
        boolean isHandle = blackList.stream().anyMatch(
                b -> {
                    String[] temp = splitBlackListSelector(b);
                    By by = findBy(temp[0], temp[1]);
                    List<WebElement> elements = findElements(by, true, -1);
                    if (elements.size() > 0) {
                        click0(elements.get(0));
                        return true;
                    }
                    return false;
                }
        );
        setTimeOut0(10, TimeUnit.SECONDS);
        return isHandle;
    }

    /**
     * 封装通用异常处理，需要走异常处理的方法都需要通过此方法来执行
     * @param method
     * @param args
     * @return
     */
    private Object proxyHandleException(Method method, Object... args) {
        try {
            return invokeMethod(method, this, args);
        } catch (TimeoutException |
                StaleElementReferenceException |
                ArrayIndexOutOfBoundsException |
                NoSuchElementException e) {
            if (envConfig.getConfig().isEnableHandleException() && handleException()) {
                return invokeMethod(method, this, args);
            } else {
                throw e;
            }
        }
    }

    /**
     * 切割blackList
     * @param selector
     * @return
     */
    private String[] splitBlackListSelector(String selector) {
        return selector.substring(1, selector.length() - 1).split(",");
    }

    /**
     * 1、记录定位方式的别名和全名
     * 2、记录定位方式属于By还是MobileBy
     * 3、记录定位方式的参数类型（其实不写也可以）
     */
    private enum PageBy {
        ID(FIND_ID, "id"),
        XPATH(FIND_XPATH, "xpath"),
        AID(FIND_AID,"aid"),
        UIAUTOMATOR(FIND_UIAUTOMATOR, "uiautomator"),
        ;
        private final Method method;
        private final String alias;
        PageBy(Method method, String alias) {
            this.method = method;
            this.alias = alias;
        }
    }

    /**
     * 1、记录元素操作的封装方法
     * 3、记录元素操作方法的别名
     */
    private enum PageOperate {
        CLEAR(BasePageHelper.CLEAR, "clear"),
        CLICK(BasePageHelper.CLICK, "click"),
        SEND_KEYS(BasePageHelper.SEND_KEYS, "input"),
        GET_TEXT(BasePageHelper.GET_TEXT,  "text"),
        GET_TEXTS(BasePageHelper.GET_TEXTS, "texts"),
        GET_ATTRIBUTE(BasePageHelper.GET_ATTRIBUTE, "attribute"),
        ;
        private final String alias;
        private final Method method;
        PageOperate(Method method, String alias) {
            this.alias = alias;
            this.method = method;
        }
    }


}
