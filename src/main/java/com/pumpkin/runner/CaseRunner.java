package com.pumpkin.runner;

import com.pumpkin.runner.structure.*;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.List;
import java.util.Map;

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
        Map<String, Object> caseTrueData = caseMethod.getCaseTrueData();
        Map<String, Object> assertTrueData = caseMethod.getAssertTrueData();

        caseSteps.forEach(caseStep -> runPageObjectStructure(caseStep, caseTrueData));
        asserts.forEach(assertStep -> runAssert(assertStep, assertTrueData));
    }

    private static void runPageObjectStructure(PageObjectStructure poStructure, Map<String, Object> caseTrueData) {
        /**
         * 1、取出case传递给po方法的参数
         * 2、执行po方法
         */
        List<ElementStructure> poSteps = poStructure.getPoSteps();
        List<String> params = poStructure.getParams();
        List<String> caseToPOParams = poStructure.getCaseToPOParams();

        CaseInsensitiveMap<String, Object> poTrueData = new CaseInsensitiveMap<>();
        for (int i = 0; i < caseToPOParams.size(); ++i) {
            Object obj = caseTrueData.get(caseToPOParams.get(i));
            poTrueData.put(params.get(i), obj);
        }

        poSteps.forEach(poStep -> runPOStep(poStep, poTrueData));
    }

    private static void runPOStep(ElementStructure poStep, Map<String, Object> poTrueData) {
        /**
         * 1、根据特定平台的定位符查找元素
         * 2、判断指定action所需的参数是否都有传递
         * 3、根据指定action操作元素
         */
        Map<String, ElementSelector> selectors = poStep.getSelectors();
        String action = poStep.getAction();
        List<String> data = poStep.getData();


    }

    private static void runAssert(Assert caseAssert, Map<String, Object> assertTrueData) {
        /**
         * 执行assert
         */
    }
}
