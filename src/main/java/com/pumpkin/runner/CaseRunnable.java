package com.pumpkin.runner;

import com.pumpkin.runner.structure.CaseStructure;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @className: CaseRunnable
 * @description: 解析好的case存储结构
 * @author: pumpkin
 * @date: 2021/5/22 11:30 上午
 * @version: 1.0
 **/
@Data
@Builder
public class CaseRunnable {
    //case文件名
    private String caseFileName;
    /**
     * @BeforeEach方法
     */
    //全部的用例,包含全部的@BeforeEach和@AfterEach
    private List<CaseStructure> cases;
    /**
     * @AfterEach方法
     */
}
