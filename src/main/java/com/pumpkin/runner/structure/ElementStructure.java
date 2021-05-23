package com.pumpkin.runner.structure;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @className: ElementStructure
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/22 9:22 下午
 * @version: 1.0
 **/
@Data
@Builder
public class ElementStructure {
    private List<String> params;
    /**
     * 存储各个平台的定位方式
     */
    private CaseInsensitiveMap<String, ElementSelector> selectors;
    private String action;

    /**
     * sendKeys中使用
     */
    private String keyword;
    private String replace;

    /**
     * 元素操作枚举类
     */
    enum Action {
        CLICK("click", "CLICK"),
        INPUT("input", "SEND_KEYS"),
        ;
        String[] aliases;
        Action(String... alias) {
            this.aliases = alias;
        }
        public boolean isAlias(String alias) {
            return Arrays.stream(aliases).anyMatch(a -> a.equals(alias));
        }
    }

    /**
     * 定位元素、操作元素内部类
     */
    static class ElementOperate {
        private final static Method CLICK;
        private final static Method SEND_KEYS;
        static {
            CLICK = null;
            SEND_KEYS = null;
        }
        public Object invoke(Action action, Object... args) {

            return null;
        }
    }
}
