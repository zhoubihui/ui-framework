package com.pumpkin.runner.structure;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @className: PageObjectStructure
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/22 6:40 下午
 * @version: 1.0
 **/
@Data
@Builder
public class PageObjectStructure {
    /**
     * PO方法所属的文件名
     */
    private String pageFileName;
    /**
     * PO方法的名称
     */
    private String name;
    /**
     * 方法的参数,需要替换的是这个,为空则说明该PO方法是无参方法
     */
    private List<String> params;
    /**
     * 方法的步骤
     */
    private List<ElementStructure> poSteps;
}
