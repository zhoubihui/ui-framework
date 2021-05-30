package com.pumpkin.core;

import com.pumpkin.runner.ICaseRunnable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

class DriverManagerTest {

    @Test
    void getDriver() {
        ICaseRunnable.Env env = ICaseRunnable.Env.builder().platform("android").targetApp("wework").build();
        String caseFileName = "case/search/search-case.yaml";
        WebDriver driver = DriverManager.getInstance().getDriver(caseFileName, env);
        Assertions.assertNotNull(driver);
    }
}