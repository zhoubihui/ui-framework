package com.pumpkin.model.selector;

import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @className: ElementSelectorModel
 * @description: xxx-selector.yaml中关于元素定位符的描述
 * @author: pumpkin
 * @date: 2021/5/21 9:20 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class ElementSelectorModel implements Model {
    private String strategy;
    private String selector;
    private boolean multiple;
    private int index;
}
