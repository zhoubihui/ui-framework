package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @className: SelectorModel
 * @description: xxx-selector.yaml文件的定位方式和定位符表达
 * @author: pumpkin
 * @date: 2021/5/20 11:09 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class SelectorModel {
    private Map<String, Object> config;
    private Map<String, Map<String, List<String>>> selectors;
}
