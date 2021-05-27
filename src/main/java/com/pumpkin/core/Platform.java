package com.pumpkin.core;

import java.util.Arrays;

/**
 * @className: Platform
 * @description: 平台enum
 * @author: pumpkin
 * @date: 2021/5/27 8:18 上午
 * @version: 1.0
 **/
public enum Platform {
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
