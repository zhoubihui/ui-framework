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
     * case传递给po方法的参数和po定义的参数按顺序来确定
     * 注意：有顺序要求，无名称要求，举例：
     * case: ${search-page.search(${keyword},${replace})}
     * po:
     *  params:
     *      - replace
     *      - keyword
     * 那么按位置对应，${keyword}的值给replace
     */
    private List<String> params;

    /**
     * case传递给PO的参数顺序
     */
    private List<String> caseToPOParams;

    /**
     * 方法的步骤
     */
    private List<ElementStructure> poSteps;
}
