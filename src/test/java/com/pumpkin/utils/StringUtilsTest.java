package com.pumpkin.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @ParameterizedTest
    @MethodSource("matcherData")
    void matcher(String src, String regex) {
        String result = StringUtils.matcher(src, regex);
        System.out.println(result);
        Assertions.assertFalse(result.isBlank());
    }

    static Stream<Arguments> matcherData() {
        return Stream.of(
                Arguments.of("${message-page.to-search(${keyword}, ${replace})}", "\\((.+?)\\)"),
                Arguments.of("${message-page.to-search()}", "\\((.+?)\\)"),
                Arguments.of("${message-page.to-search}", "\\((.+?)\\)")
        );
    }

    @ParameterizedTest
    @MethodSource("matchersData")
    void matchers(String src, String regex) {
        List<String> result = StringUtils.matchers(src, regex);
        System.out.println(result);
    }

    static Stream<Arguments> matchersData() {
        return Stream.of(
                Arguments.of("${keyword}, ${replace}", "\\{(.+?)\\}.+\\{(.+?)\\}")
        );
    }
}