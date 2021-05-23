package com.pumpkin.runner.structure;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @className: CaseMethod
 * @description:
 * @author: pumpkin
 * @date: 2021/5/22 6:31 下午
 * @version: 1.0
 **/
@Data
@Builder
public class CaseMethod {
    /**
     * case的方法名
     */
    private String name;
    /**
     * case需要传入的参数
     */
    private List<String> params;
    /**
     * 对应xxx-case.yaml文件中的case下的steps关键字
     */
    private List<PageObjectStructure> steps;
    /**
     * 对应xxx-case.yaml文件中的case下的assert关键字
     */
    private List<Assert> asserts;
}
