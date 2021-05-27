package com.pumpkin.core;

import com.pumpkin.runner.structure.Env;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlatformConfigParseTest {

    @Test
    void getConfig() {
        CaseInsensitiveMap<String, Object> config =
                PlatformConfigParse.getConfig(Env.builder().platform("Android").targetApp("wework").build());
        System.out.println(config);
    }
}