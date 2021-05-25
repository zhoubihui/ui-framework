package com.pumpkin.runner.structure;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @className: CaseMethod
 * @description:
 * @author: pumpkin
 * @date: 2021/5/22 6:31 下午
 * @version: 1.0
 **/
@Data
@Builder
public class CaseMethod implements Serializable {
    /**
     * case的方法名
     */
    private String name;

    /**
     * case的params部分定义的参数，用于参数替换时做校验
     */
    private List<String> params;

    /**
     * 对应xxx-case.yaml文件中的case下的steps关键字
     */
    private List<PageObjectStructure> caseSteps;

    /**
     * 对应xxx-case.yaml文件中的case下的assert关键字
     */
    private List<Assert> asserts;

    /**
     * case中使用的参数，目的是和断言参数区分开
     */
    private Set<String> caseParams;

    /**
     * 断言中使用的参数
     */
    private Set<String> assertParams;

    /**
     * 存储当前case.steps中需要的参数，和参数对应的值
     */
    private CaseInsensitiveMap<String, Object> caseTrueData;

    /**
     * 存储当前case.assert中需要的参数，和参数对应的值
     */
    private CaseInsensitiveMap<String, Object> assertTrueData;
}
