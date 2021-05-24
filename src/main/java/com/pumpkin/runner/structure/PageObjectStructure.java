package com.pumpkin.runner.structure;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
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
public class PageObjectStructure implements Serializable {
    /**
     * PO方法所属的文件名
     */
    private String pageFileName;
    /**
     * PO方法的名称
     */
    private String name;
    /**
     * 方法的形参,为空则说明该PO方法是无参方法,并且用List的顺序来表示方法的顺序
     * 注意：这里的顺序和个数应该和PO方法中定义的params部分一致
     */
    private List<String> params;
    /**
     * 方法的步骤
     */
    private List<ElementStructure> poSteps;
}
