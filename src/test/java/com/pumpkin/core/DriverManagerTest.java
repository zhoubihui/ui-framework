package com.pumpkin.core;

import com.pumpkin.runner.ICaseRunnable;
import com.pumpkin.runner.PageRunner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DriverManagerTest {

    @Test
    void getDriver() {
        ICaseRunnable.Env env = ICaseRunnable.Env.builder().platform("android").targetApp("xueqiu").build();
        String caseFileName = "case/search/search-case.yaml";
        PageRunner pageRunner = PageManager.getInstance().getPageRunner(caseFileName, env);
        System.out.println(pageRunner.getEnvConfig());
    }
}