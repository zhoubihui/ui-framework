package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @className: IPublic
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/30
 * @version: 1.0
 **/
public interface IPublic {
    @Data
    @Accessors(chain = true)
    class EnvModel implements IModel {
        private String platform;
        private String targetApp;
    }

    @Data
    @Accessors(chain = true)
    class UrlConfigModel {
        private String pageUrl;
        private String selectorUrl;
        private String dataUrl;
    }
}
