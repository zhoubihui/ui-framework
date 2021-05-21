package com.pumpkin.model.selector;

import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private CaseInsensitiveMap<String, Map<String, List<String>>> selectors;

    /**
     * 获取指定平台，指定元素名称的定位方法和定位符
     * @param platform
     * @param name
     * @return
     */
    public List<String> getSelector(String platform, String name) {
        Map<String, List<String>> map = selectors.get(platform);
        if (Objects.isNull(map))
            return null;
        return map.get(name);
    }
}
