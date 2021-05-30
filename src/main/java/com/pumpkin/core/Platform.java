package com.pumpkin.core;

import java.util.Arrays;

/**
 * @className: Platform
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/30
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
