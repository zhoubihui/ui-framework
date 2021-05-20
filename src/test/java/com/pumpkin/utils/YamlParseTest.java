package com.pumpkin.utils;

import com.pumpkin.model.AppConfigModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class YamlParseTest {

    @Test
    void readValue() throws IOException {
        AppConfigModel model = YamlParse.readValue("config/app-config.yaml", AppConfigModel.class);
        System.out.println(model);
    }
}