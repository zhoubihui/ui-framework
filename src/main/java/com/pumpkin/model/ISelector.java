package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.Map;

/**
 * @className: ISelectorModel
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/30
 * @version: 1.0
 **/
public interface ISelector {
    @Data
    @Accessors(chain = true)
    class SelectorModel implements IModel {
        private CaseInsensitiveMap<String, CaseInsensitiveMap<String, ElementSelectorModel>> selectors;

        /**
         * 获取指定平台，指定元素名称的定位符相关的
         *
         * @param name
         * @return
         */
        public Map<String, ElementSelectorModel> getSelector(String name) {
            return selectors.get(name);
        }
    }

    @Data
    @Accessors(chain = true)
    class ElementSelectorModel {
        private String strategy;
        private String selector;
        private boolean multiple;
        private int index;
    }
}
