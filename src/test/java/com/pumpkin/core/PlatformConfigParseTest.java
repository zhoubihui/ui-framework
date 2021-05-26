package com.pumpkin.core;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlatformConfigParseTest {

    @Test
    void getConfig() {
        CaseInsensitiveMap<String, Object> config =
                PlatformConfigParse.getConfig("Android", "wework");
        System.out.println(config);
    }
}