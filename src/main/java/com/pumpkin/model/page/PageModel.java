package com.pumpkin.model.page;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.List;
import java.util.Map;

/**
 * @className: PageModel
 * @description: xxx-page.yaml文件的表达
 * @author: pumpkin
 * @date: 2021/5/21 7:10 上午
 * @version: 1.0
 **/
public class PageModel {
    private CaseInsensitiveMap<String, Map<String, List<Step>>> methods;

    /**
     * 获取指定方法名的数据
     * @param name
     * @return
     */
    public Map<String, List<Step>> getMethod(String name) {
        return methods.get(name);
    }
}
