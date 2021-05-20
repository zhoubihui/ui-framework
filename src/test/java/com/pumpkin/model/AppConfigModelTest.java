package com.pumpkin.model;

import org.junit.jupiter.api.Test;

/**
 * @className: AppConfigModelTest
 * @description: AppConfigModel单元测试
 * @author: pumpkin
 * @date: 2021/5/20 9:30 上午
 * @version: 1.0
 **/
public class AppConfigModelTest {
    @Test
    void createTest() {
        AppConfigModel appConfigModel = new AppConfigModel();
        System.out.println(appConfigModel);
    }
}
