package com.pumpkin.utils;

import com.pumpkin.model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

class YamlParseTest {

    @ParameterizedTest
    @MethodSource("readValue")
    void readValue(String path, Class<IModel> clazz) throws IOException {
        IModel model = YamlParse.readValue(path, clazz);
        System.out.println(model);
    }

    static Stream<Arguments> readValue() {
        return Stream.of(
                Arguments.of("app-config.yaml", IConfig.AppConfigModel.class),
                Arguments.of("page/search/search-page.yaml", IPage.PageModel.class),
                Arguments.of("page/search/message-page.yaml", IPage.PageModel.class),
                Arguments.of("selector/search/search-selector.yaml", ISelector.SelectorModel.class),
                Arguments.of("data/search/search-data.yaml", IData.DataModel.class),
                Arguments.of("case/search/search-case.yaml", ICase.CaseModel.class)
        );
    }
}