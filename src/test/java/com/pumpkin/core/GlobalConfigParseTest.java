package com.pumpkin.core;

import com.pumpkin.model.IConfig;
import org.junit.jupiter.api.Test;

class GlobalConfigParseTest {
    @Test
    void getGlobalConfig() {
        IConfig.GlobalConfigModel globalConfig = GlobalConfigParse.getGlobalConfig();
        System.out.println(globalConfig);
    }
}