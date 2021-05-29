package com.pumpkin.runner;

import com.pumpkin.runner.structure.CaseStructure;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @className: CaseRunnable
 * @description: 解析好的case存储结构
 * @author: pumpkin
 * @date: 2021/5/22 11:30 上午
 * @version: 1.0
 **/
@Data
@Builder
public class CaseRunnable implements Serializable {
    //case文件名
    private String caseFileName;
    /**
     * @BeforeAll方法
     */

    /**
     * xxx-case.yaml中的cases字段，每个CaseStructure对象代表一个case，即一个测试方法
     */
    private List<CaseStructure> cases;

    /**
     * @AfterAll方法
     */

    /**
     * 这个case文件的运行环境
     */
    private Env env;

    @Data
    @Builder
    public static class Env {
        private String platform;
        private String targetApp;
    }
}
