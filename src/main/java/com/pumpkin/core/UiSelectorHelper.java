package com.pumpkin.core;

import com.pumpkin.utils.ReflectUtils;
import com.pumpkin.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @className: UiSelectorHelper
 * @description: Android App工具类
 * @author: zhoubihui
 * @date: 2021/5/14 9:49 下午
 * @version: 1.0
 **/
public class UiSelectorHelper {
    //支持的xpath函数
    private static final List<String> supportXpathFunc = Arrays.asList("contains");
    //支持的定位方式
    private static final List<String> supportStrategy = Arrays.asList("ByXPath", "ById", "ByAccessibilityId");
    //不支持的xpath语法：or函数，从子元素找父元素
    private static final List<String> noSupportRegex = Arrays.asList("\\b(or)\\b", "(/\\.)");

    private static final String UI_SELECTOR = "new UiSelector().";

    /**
     * 解析xpath或定位符号,并转成UiSelector的语法
     * 注意：xpath目前只支持相对路径,并且不支持父元素、子元素的查找
     * @param strategy 定位方式,Id,XPath等
     * @param selector 定位符
     * @return Stream中每个元素就是一个UiSelector的方法调用语法
     */
    public static String transformUiSelector(String strategy, String selector) {
        //todo 校验错误的xpath表达式
        if (Objects.isNull(strategy) || strategy.isBlank() ||
                noSupportRegex.stream().anyMatch(r -> StringUtils.isMatcher(selector, r)) ||
                supportStrategy.stream().noneMatch(s -> s.equals(strategy))
            ) {
            return "";
        }
        //这里转换成UiSelector的语法
        String trueSelector = "";
        Method method = ReflectUtils.findMethod(UiSelectorHelper.class, "transform" + strategy, String.class);
        try {
            trueSelector = (String) method.invoke(null, selector);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();

        }
        return trueSelector;
    }

    /**
     * resource-id查找转换
     * @param selector
     * @return
     */
    private static String transformById(String selector) {
        return UI_SELECTOR + UiSelector.RESOURCE_ID.getName() + "(\"" + selector + "\")";
    }

    /**
     * 解析xpath
     * //android.widget.RelativeLayout//*[@text='周南瓜']
     * new UiSelector().className("android.widget.RelativeLayout").childSelector(new UiSelector().text("周南瓜"))
     * @param xpath
     * @return
     */
    private static String transformByXPath(String xpath) {
        if (!(xpath.startsWith("//*") || xpath.startsWith("//")))
            return "";
        int index = 0;
        int endIndex = -1;
        String tempStr = null;
        List<String> condition = new ArrayList<>();
        while (index < xpath.length()) {
            //1、判断起始点
            index = xpath.startsWith("//*", index) ? index + 3 : (xpath.startsWith("//", index) ? index + 2 : 0);
            if (xpath.charAt(index) == '[') {
                //2、找到左右括号，并截取左右括号之间的字符串,[开头的字符不需要取[]，这里根据下标做了舍弃
                endIndex = xpath.indexOf("]", index);
                tempStr = xpath.substring(index + 1, endIndex);
                //是]需要+1，下一次循环时就不会从]开始
                endIndex += 1;
            } else {
                //判断]有没有
                int index0 = xpath.indexOf("]", index);
                int index1 = xpath.indexOf("/", index);
                if (index0 == -1 && index1 == -1)
                    endIndex = xpath.length();
                else
                    endIndex = index0 == -1 ? index1 : (index1 == -1 ? index0 + 1 :
                            (index0 < index1 ? index0 + 1 : index1));
                //]需要加1，因为需要这个右括号在下面的逻辑一起处理
                tempStr = xpath.substring(index, endIndex);
            }
            index = endIndex;
            //2、处理tempStr
            if (tempStr.startsWith("@")) {
                condition.add(parseAttribute(tempStr));
                continue;
            }
            int funcIndex = tempStr.indexOf("(");
            if (funcIndex == -1) {
                condition.add(parseClassName(tempStr));
                continue;
            }
            condition.add(parseXpathFunction(tempStr));
        }
        return mergeCondition(condition);
    }

    /**
     * 将父子查询的多个UiSelector串联起来
     * @param condition
     * @return
     */
    private static String mergeCondition(List<String> condition) {
        StringBuilder builder = new StringBuilder();
        builder.append(condition.get(0));
        for (int i = 1; i < condition.size(); ++i) {
            builder.append(".childSelector(").append(condition.get(i)).append(")");
        }
        return builder.toString();
    }

    /**
     * 处理xpath的函数,传入contains(@text, '交易')等
     * 注意：目前只支持contains，支持传入的形式有：
     * 1、contains(@text, '交易') and @resource-id='com.xueqiu.android:id/tab_name'
     * 2、contains(@text, '交易') and contains(@content-desc, '交易')
     * @param xpathFunction
     * @return new UiSelector().xxx().yyy()...
     */
    private static String parseXpathFunction(String xpathFunction) {
        StringBuilder funcBuilder = new StringBuilder();
        funcBuilder.append(UI_SELECTOR);
        String[] funcParam = StringUtils.split(xpathFunction, "\\b(and)\\b");

        Arrays.stream(funcParam).forEach(
                f -> {
                    if (f.startsWith("@")) {
                        funcBuilder.append(parseSingleAttribute(f)).append(".");
                    } else {
                        funcBuilder.append(parseSingleXpathFunction(f)).append(".");
                    }
                }
        );
        funcBuilder.deleteCharAt(funcBuilder.length() - 1);
        return funcBuilder.toString();
    }

    private static String parseSingleXpathFunction(String xpathFunction) {
        StringBuilder builder = new StringBuilder();
        int startIndex = xpathFunction.indexOf("(");
        int endIndex = xpathFunction.indexOf(")");
        String func = xpathFunction.substring(0, startIndex); //函数名
        String[] params = StringUtils.split(xpathFunction.substring(startIndex + 1, endIndex), ",");
        String trueFunc = supportXpathFunc.stream().filter(s -> s.equals(func)).findFirst().orElse("contains");
        if ("contains".equals(trueFunc) && ("@resource-id".equals(params[0]) || "@class".equals(params[0]))) {
            builder.append(parseSingleAttribute(params[0] + "=" + params[1]));
            return builder.toString();
        }
        String trueAttr = params[0].substring(1);
        UiSelector uiSelector = Arrays.stream(UiSelector.values()).filter(s -> s.isAlias(trueAttr + "-" + trueFunc)).
                findFirst().orElse(UiSelector.RESOURCE_ID);
        builder.append(uiSelector.getName()).append("(\"").
                append(params[1].substring(1, params[1].length() - 1)).append("\")");
        return builder.toString();
    }

    /**
     * 解析属性,支持传入的形式有：
     * 1、@resource-id='com.xueqiu.android:id/tab_name'
     * 2、@resource-id='com.xueqiu.android:id/tab_name' and @text='交易'
     * 3、@resource-id='com.xueqiu.android:id/tab_name' and contains(@text, '交易')
     * 注意：如果没匹配到默认使用resource-id
     * @param xpathAttr
     * @return 返回格式：new UiSelector().xxx().yyy()...
     */
    private static String parseAttribute(String xpathAttr) {
        StringBuilder attrBuilder = new StringBuilder();
        attrBuilder.append(UI_SELECTOR);
        String[] attrs = StringUtils.split(xpathAttr, "\\b(and)\\b");
        Arrays.stream(attrs).forEach(
                a -> {
                    if (a.startsWith("@")) {
                        attrBuilder.append(parseSingleAttribute(a)).append(".");
                    } else {
                        attrBuilder.append(parseSingleXpathFunction(a)).append(".");
                    }
                }
        );
        attrBuilder.deleteCharAt(attrBuilder.length() - 1);
        return attrBuilder.toString();
    }

    private static String parseSingleAttribute(String attr) {
        StringBuilder builder = new StringBuilder();
        String[] attrParam = StringUtils.split(attr.substring(1), "=");
        UiSelector uiSelector = Arrays.stream(UiSelector.values()).filter(s -> s.isAlias(attrParam[0])).findFirst().
                orElse(UiSelector.RESOURCE_ID);
        builder.append(uiSelector.getName()).append("(\"").
                append(attrParam[1], 1, attrParam[1].length() - 1).append("\")");
        return builder.toString();
    }

    /**
     * 解析类名，支持传入的形式有：
     * 1、android.widget.ListView
     * 2、android.widget.ListView[@text='交易']
     * @param xpathClass
     * @return new UiSelector().xxx().yyy()...
     */
    private static String parseClassName(String xpathClass) {
        StringBuilder builder = new StringBuilder();
        builder.append(UI_SELECTOR);
        //1、判断有没有属性条件
        int index = xpathClass.indexOf("[");
        String className = index == -1 ? xpathClass : xpathClass.substring(0, index);
        builder.append(UiSelector.CLASSNAME.getName()).append("(\"").append(className).append("\")");
        if (index != -1) {
            //+1和-1的目的是：去掉[和]
            String attr = xpathClass.substring(index + 1, xpathClass.length() - 1);
            builder.append(".").append(parseSingleAttribute(attr));
        }
        return builder.toString();
    }

    /**
     * content-desc查找转换
     * @param selector
     * @return
     */
    private static String transformByAccessibilityId(String selector) {
        return UI_SELECTOR + UiSelector.DESCRIPTION.getName() + "(\"" + selector + "\")";
    }

    enum UiSelector {
        DESCRIPTION("description", "content-desc"),
        DESCRIPTION_CONTAINS("descriptionContains", "content-desc-contains"),
        RESOURCE_ID("resourceId", "resource-id"),
        TEXT("text"),
        TEXT_CONTAINS("textContains", "text-contains"),
        CHILD_SELECTOR("childSelector"),
        CLASSNAME("className", "class"),
        ;

        String[] aliases;
        UiSelector(String... aliases) {
            this.aliases = aliases;
        }

        public String getName() {
            return this.aliases[0];
        }
        public boolean isAlias(String alias) {
            return Arrays.stream(aliases).anyMatch(a -> a.equals(alias));
        }
    }
}
