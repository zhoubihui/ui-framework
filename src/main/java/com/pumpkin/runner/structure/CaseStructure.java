package com.pumpkin.runner.structure;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @className: CaseStructure
 * @description: case结构，只包含case本身
 * @author: pumpkin
 * @date: 2021/5/22 6:33 下午
 * @version: 1.0
 **/
@Data
@Builder
public class CaseStructure {
    /**
     * @BeforeEach方法
     */
    //全部的用例
    private List<CaseMethod> cases;
    /**
     * @AfterEach方法
     */
}
