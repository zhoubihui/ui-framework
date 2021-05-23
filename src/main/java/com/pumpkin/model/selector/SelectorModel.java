package com.pumpkin.model.selector;

import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.Map;

/**
 * @className: SelectorModel
 * @description: xxx-selector.yaml文件表达
 * @author: pumpkin
 * @date: 2021/5/20 11:09 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class SelectorModel implements Model {
    private CaseInsensitiveMap<String, CaseInsensitiveMap<String, ElementSelectorModel>> selectors;

    /**
     * 获取指定平台，指定元素名称的定位符相关的
     * @param name
     * @return
     */
    public Map<String, ElementSelectorModel> getSelector(String name) {
        return selectors.get(name);
    }
}
