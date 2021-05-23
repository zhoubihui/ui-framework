package com.pumpkin.utils;

import com.pumpkin.core.GlobalConfigParse;
import com.pumpkin.model.config.GlobalConfigModel;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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