package com.pumpkin.utils;

import com.pumpkin.core.GlobalConfigParse;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    @Test
    void getFilePathFromDirectory() {
        String path = FileUtils.getFilePathFromDirectory(
                GlobalConfigParse.getGlobalConfig().getBase().getCaseDirectory(),
                "wework-search-case");
        System.out.println(path);
        Assertions.assertTrue(StringUtils.isNotBlank(path));
    }
}