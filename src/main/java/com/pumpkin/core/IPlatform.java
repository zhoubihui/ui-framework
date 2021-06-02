package com.pumpkin.core;

import java.util.Arrays;

/**
 * @className: IPlatform
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/6/2
 * @version: 1.0
 **/
public interface IPlatform {
    /**
     * 具体大平台
     */
    enum Platform {
        APP("android", "ios"),
        WEB("web"),
        ;
        String[] aliases;
        Platform(String... alias) {
            this.aliases = alias;
        }

        public boolean isAlias(String alias) {
            return Arrays.stream(aliases).anyMatch(a -> a.equals(alias.toLowerCase()));
        }
    }

    /**
     * App这个大平台下的系统
     */
    enum AppPlatform {
        ANDROID("android"),
        iOS("ios"),
        ;
        private final String alias;
        AppPlatform(String alias) {
            this.alias = alias;
        }
        public boolean isAlias(String alias) {
            return this.alias.equalsIgnoreCase(alias);
        }
        public String getAlias() {
            return this.alias;
        }
    }
}
