package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.Map;
import java.util.Objects;

/**
 * @className: IConfigModel
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/30
 * @version: 1.0
 **/
public interface IConfig {
    @Data
    @Accessors(chain = true)
    class GlobalConfigModel implements IModel {
        private GlobalBaseModel base;
    }
    @Data
    @Accessors(chain = true)
    class GlobalBaseModel {
        private String caseDirectory = "case/";
        private String dataDirectory = "data/";
        private String pageDirectory = "page/";
        private String selectorDirectory = "selector/";

        /**
         * 获取所属分组的相对路径
         * @return
         */
        public String getBaseDirectory(String fileName) {
            String baseDirectory = "";
            switch (fileName.split("-")[1].toUpperCase()) {
                case "CASE":
                    baseDirectory = caseDirectory;
                    break;
                case "DATA":
                    baseDirectory = dataDirectory;
                    break;
                case "PAGE":
                    baseDirectory = pageDirectory;
                    break;
                case "SELECTOR":
                    baseDirectory = selectorDirectory;
                    break;
            }
            return baseDirectory;
        }
    }
    @Data
    @Accessors(chain = true)
    class AppConfigModel implements IModel {
        //存储基本配置信息
        private BaseModel base;

        //存储app相关配置信息，使用不区分大小写的Map
        private CaseInsensitiveMap<String, CaseInsensitiveMap<String, AppModel>> app;

        /**
         * 返回指定平台下的全部app设置信息
         * @param platform Android，或iOS，不区分大小写
         * @return
         */
        public Map<String, AppModel> getPlatformDetail(String platform) {
            return app.get(platform);
        }

        /**
         * 获取指定平台，指定app下的全部设置信息，不区分大小写
         * @param platform
         * @param appKey
         * @return
         */
        public AppModel getAppDetail(String platform, String appKey) {
            Map<String, AppModel> platformDetail = getPlatformDetail(platform);
            if (Objects.isNull(platformDetail))
                return null;
            return platformDetail.get(appKey);
        }
    }
    @Data
    @Accessors(chain = true)
    class BaseModel {
        private CaseInsensitiveMap<String, Object> config;
        private CaseInsensitiveMap<String, Object> caps;

        /**
         * 获取指定的capability值
         * @param capabilityName
         * @return
         */
        public Object getCapability(String capabilityName) {
            return caps.get(capabilityName);
        }
    }
    @Data
    @Accessors(chain = true)
    class AppModel {
        private CaseInsensitiveMap<String, Object> caps;
    }
}
