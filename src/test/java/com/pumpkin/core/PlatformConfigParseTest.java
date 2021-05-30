package com.pumpkin.core;

import com.pumpkin.runner.ICaseRunnable;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.junit.jupiter.api.Test;

class PlatformConfigParseTest {

    @Test
    void getConfig() {
        CaseInsensitiveMap<String, Object> config =
                PlatformConfigParse.getConfig(ICaseRunnable.Env.builder().platform("Android").targetApp("wework").build());
        System.out.println(config);
    }
}