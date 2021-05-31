package com.pumpkin.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @className: StringUtils
 * @description: String工具类
 * @author: zhoubihui
 * @date: 2021/5/15 10:14 上午
 * @version: 1.0
 **/
public class StringUtils {
    /**
     * 将字符串分割，并去掉前后空格
     * @param srcStr
     * @param regex
     * @return
     */
    public static String[] split(String srcStr, String regex) {
        return Arrays.stream(srcStr.split(regex)).map(String::trim).map(s -> new String[]{s}).
                reduce(new String[0], ArrayUtils::addAll);
    }

    public static boolean isMatcher(String input, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(input);
        return matcher.matches();
    }

    /**
     * 获取字符串匹配的子字符串，固定返回第2个分组
     * @param input
     * @param regex
     * @return
     */
    public static String matcher(String input, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(input);
        if (matcher.find())
            return matcher.group(1);
        return "";
    }

    /**
     * 获取字符串匹配的子字符串，固定返回全部获取到分组
     * @param input
     * @param regex
     * @return
     */
    public static List<String> matchers(String input, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(input);
        if (matcher.find()) {
            int count = matcher.groupCount();
            List<String> params = new ArrayList<>(count - 1);
            for (int i = 0; i < count; ++i) {
                params.add(matcher.group(i + 1));
            }
            return params;
        }
        return Collections.emptyList();
    }
}
