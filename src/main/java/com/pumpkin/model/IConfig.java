package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.List;
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
    class AppConfigModel implements IModel {
        //存储基本配置信息
        private BaseModel base;

        //存储app相关配置信息，使用不区分大小写的Map
        private CaseInsensitiveMap<String, CaseInsensitiveMap<String, BaseModel>> app;

        /**
         * 返回指定平台下的全部app设置信息
         * @param platform Android，或iOS，不区分大小写
         * @return
         */
        public Map<String, BaseModel> getPlatformDetail(String platform) {
            return app.get(platform);
        }

        /**
         * 获取指定平台，指定app下的全部设置信息，不区分大小写
         * @param platform
         * @param appKey
         * @return
         */
        public BaseModel getAppDetail(String platform, String appKey) {
            Map<String, BaseModel> platformDetail = getPlatformDetail(platform);
            if (Objects.isNull(platformDetail))
                return null;
            return platformDetail.get(appKey);
        }
    }

    @Data
    @Accessors(chain = true)
    class BaseModel {
        private ConfigModel config;
        private CaseInsensitiveMap<String, Object> caps;
    }

    @Data
    @Accessors(chain = true)
    class ConfigModel {
        private String url;
        private Boolean enabledTransformXpath;
        private Boolean enabledScroll;
        private Boolean enabledReplace;
        private Boolean enabledMethodLog;
        private Boolean enableHandleException;
        private List<String> blackList;
    }
}
