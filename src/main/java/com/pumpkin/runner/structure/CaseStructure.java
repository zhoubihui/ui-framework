package com.pumpkin.runner.structure;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.io.Serializable;
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
public class CaseStructure implements Serializable {
    /**
     * @BeforeEach方法
     */

    /**
     * case方法，这里的个数和参数的组数相同
     */
    private List<CaseMethod> cases;

    /**
     * @AfterEach方法
     */
}
