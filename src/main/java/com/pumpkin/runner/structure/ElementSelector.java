package com.pumpkin.runner.structure;

import lombok.Builder;
import lombok.Data;

/**
 * @className: ElementSelector
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/23 2:57 下午
 * @version: 1.0
 **/
@Data
@Builder
public class ElementSelector {
    private String strategy;
    private String selector;
    private boolean multiple;
    private int index;
}
