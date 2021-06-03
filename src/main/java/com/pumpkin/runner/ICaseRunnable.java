package com.pumpkin.runner;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.hamcrest.Matchers;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @className: ICaseRunnable
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/30
 * @version: 1.0
 **/
public interface ICaseRunnable {
    @Data
    @Builder
    class CaseRunnable implements Serializable {
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
    }

    @Data
    @Builder
    class CaseStructure implements Serializable {
        /**
         * @BeforeEach方法
         */

        /**
         * case方法，这里的个数和参数的组数相同
         */
        private List<CaseMethod> cases;

        /**
         * @AfterEach方法z
         */
    }

    @Data
    @Builder
    class CaseMethod implements Serializable {
        /**
         * case的方法名
         */
        private String name;

        /**
         * case的params部分定义的参数，用于参数替换时做校验
         * 注意：xxx-data.yaml和params的参数顺序不要求相同，名称和个数相同即可
         */
        private List<String> params;

        /**
         * 对应xxx-case.yaml文件中的case下的steps关键字
         */
        private List<PageObjectStructure> caseSteps;

        /**
         * 对应xxx-case.yaml文件中的case下的assert关键字
         */
        private List<Assert> asserts;

        /**
         * case中使用的参数，目的是和断言参数区分开
         */
        private Set<String> caseParams;

        /**
         * 断言中使用的参数
         */
        private Set<String> assertParams;

        /**
         * 存储当前case.steps中需要的参数，和参数对应的值
         */
        private CaseInsensitiveMap<String, Object> caseTrueData;

        /**
         * 存储当前case.assert中需要的参数，和参数对应的值
         */
        private CaseInsensitiveMap<String, Object> assertTrueData;
    }

    @Data
    @Builder
    class Assert implements Serializable {
        private Matchers matcher;
        private Object expected;
        private Object actual;
    }

    @Data
    @Builder
    class PageObjectStructure implements Serializable {
        /**
         * PO方法所属的文件名
         */
        private String pageFileName;

        /**
         * PO方法的名称
         */
        private String name;

        /**
         * case传递给po方法的参数和po定义的参数按顺序来确定
         * 注意：有顺序要求，无名称要求，举例：
         * case: ${search-page.search(${keyword},${replace})}
         * po:
         *  params:
         *      - replace
         *      - keyword
         * 那么按位置对应，${keyword}的值给replace
         */
        private List<String> params;

        /**
         * case传递给PO的参数顺序
         */
        private List<String> caseToPOParams;

        /**
         * 方法的步骤
         */
        private List<ElementStructure> poSteps;
    }

    @Data
    @Builder
    class ElementStructure implements Serializable {
        /**
         * 存储各个平台的定位方式
         */
        private CaseInsensitiveMap<String, ElementSelector> selectors;
        private String action;

        /**
         * PO方法的action操作需要的参数
         */
        private List<String> data;
    }

    @Data
    @Builder
    class ElementSelector implements Serializable {
        private String strategy;
        private String selector;
        private boolean multiple;
        private int index;
    }

    @Data
    @Builder
    class Env implements Serializable {
        private String platform;
        private String targetApp;
    }

    @Data
    @Builder
    class EnvConfig implements Serializable {
        private ICaseRunnable.Env env;
        private Config config;
        private String platformName;
        private CaseInsensitiveMap<String, Object> caps;
    }

    @Data
    @Builder
    class Config implements Serializable {
        private String url;
        private boolean enabledTransformXpath;
        private boolean enabledScroll;
        private boolean enabledReplace;
        private boolean enabledMethodLog;
        private boolean enableHandleException;
        private List<String> blackList;
        private String wait;
    }
}
