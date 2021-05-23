package com.pumpkin.core;

import com.pumpkin.model.config.GlobalConfigModel;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class GlobalConfigParseTest {
    @Test
    void getGlobalConfig() {
        GlobalConfigModel globalConfig = GlobalConfigParse.getGlobalConfig();
        System.out.println(globalConfig);
    }
}