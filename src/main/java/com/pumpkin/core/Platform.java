package com.pumpkin.core;

import java.util.Arrays;

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
