package com.pumpkin.utils;

import com.pumpkin.model.Model;
import com.pumpkin.model.cases.CaseModel;
import com.pumpkin.model.config.AppConfigModel;
import com.pumpkin.model.data.DataModel;
import com.pumpkin.model.page.PageModel;
import com.pumpkin.model.selector.SelectorModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

class YamlParseTest {

    @ParameterizedTest
    @MethodSource("readValue")
    void readValue(String path, Class<Model> clazz) throws IOException {
        Model model = YamlParse.readValue(path, clazz);
        System.out.println(model);
    }

    static Stream<Arguments> readValue() {
        return Stream.of(
                Arguments.of("app-config.yaml", AppConfigModel.class),
                Arguments.of("page/search/search-page.yaml", PageModel.class),
                Arguments.of("page/search/message-page.yaml", PageModel.class),
                Arguments.of("selector/search/search-selector.yaml", SelectorModel.class),
                Arguments.of("data/search/search-data.yaml", DataModel.class),
                Arguments.of("case/search/search-case.yaml", CaseModel.class)
        );
    }
}