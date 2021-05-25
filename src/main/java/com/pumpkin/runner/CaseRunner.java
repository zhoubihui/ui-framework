package com.pumpkin.runner;

import com.pumpkin.runner.structure.*;

import java.util.List;

/**
 * @className: CaseRunner
 * @description: case运行器
 * @author: pumpkin
 * @date: 2021/5/22 11:30 上午
 * @version: 1.0
 **/
public class CaseRunner {
    public static void runCase(CaseRunnable caseRunnable) {
        /**
         * 1、判断cases是否有数据，有才继续往下执行
         * 2、执行@BeforeAll
         * 3、执行@BeforeEach
         * 4、执行case
         * 5、执行@AfterEach
         * 6、执行@AfterAll
         * 注意：345是循环执行
         */
        List<CaseStructure> cases = caseRunnable.getCases();
        if (cases.isEmpty())
            return;

        cases.forEach(CaseRunner::runCaseStructure);
    }

    private static void runCaseStructure(CaseStructure caseStructure) {
        /**
         * 1、执行@BeforeEach
         * 2、执行case
         * 3、执行@AfterEach
         */
        List<CaseMethod> cases = caseStructure.getCases();
        cases.forEach(CaseRunner::runCaseMethod);
    }

    private static void runCaseMethod(CaseMethod caseMethod) {
        /**
         * 1、执行caseSteps
         * 2、执行asserts
         */
        List<PageObjectStructure> caseSteps = caseMethod.getCaseSteps();
        List<Assert> asserts = caseMethod.getAsserts();

        caseSteps.forEach(CaseRunner::runPageObjectStructure);
        asserts.forEach(CaseRunner::runAssert);
    }

    private static void runPageObjectStructure(PageObjectStructure poStructure) {
        /**
         * 1、执行PO方法
         */
        List<ElementStructure> poSteps = poStructure.getPoSteps();

        poSteps.forEach(CaseRunner::runPOStep);
    }

    private static void runPOStep(ElementStructure poStep) {
        /**
         * 1、根据特定平台的定位符查找元素
         * 2、根据指定action操作元素
         */
    }

    private static void runAssert(Assert caseAssert) {
        /**
         * 执行assert
         */
    }
}
