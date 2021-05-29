package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.List;

/**
 * @className: ICaseModel
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/30
 * @version: 1.0
 **/
public interface ICase {
    @Data
    @Accessors(chain = true)
    class CaseModel implements IModel {
        /**
         * 定义这个case文件运行的环境
         */
        private IPublic.EnvModel env;

        /**
         * 定义依赖文件路径
         */
        private IPublic.UrlConfigModel config;

        /**
         * 测试方法
         */
        private List<CaseInsensitiveMap<String, CaseMethodModel>> cases;
    }

    @Data
    @Accessors(chain = true)
    class CaseMethodModel {
        private List<String> params;
        private List<String> steps;
        private List<CaseAssertModel> asserts;
    }

    @Data
    @Accessors(chain = true)
    class CaseAssertModel {
        private String type;
        private String expected;
        private String actual;
    }
}
