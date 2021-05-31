package com.pumpkin.runner;

import com.pumpkin.core.PageManager;
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

    public static void runCase(ICaseRunnable.CaseRunnable caseRunnable) {
        /**
         * 1、判断cases是否有数据，有才继续往下执行
         * 2、确定用例的执行平台信息
         * 3、执行@BeforeAll
         * 4、执行@BeforeEach
         * 5、执行case
         * 6、执行@AfterEach
         * 7、执行@AfterAll
         *  注意：345是循环执行
         */
        List<ICaseRunnable.CaseStructure> cases = caseRunnable.getCases();
        ICaseRunnable.Env env = caseRunnable.getEnv();
        String caseFileName = caseRunnable.getCaseFileName();
        if (cases.isEmpty())
            return;
        PageRunner pageRunner = PageManager.getInstance().getPageRunner(caseFileName, env);

        cases.forEach(caseStructure -> runCaseStructure(pageRunner, caseStructure));
    }

    private static void runCaseStructure(PageRunner pageRunner, ICaseRunnable.CaseStructure caseStructure) {
        /**
         * 1、执行@BeforeEach
         * 2、执行case
         * 3、执行@AfterEach
         */
        List<ICaseRunnable.CaseMethod> cases = caseStructure.getCases();
        cases.forEach(caseMethod -> runCaseMethod(pageRunner, caseMethod));
    }

    private static void runCaseMethod(PageRunner pageRunner, ICaseRunnable.CaseMethod caseMethod) {
        /**
         * 1、执行caseSteps
         * 2、执行asserts
         */
        List<ICaseRunnable.PageObjectStructure> caseSteps = caseMethod.getCaseSteps();
        List<ICaseRunnable.Assert> asserts = caseMethod.getAsserts();
        Map<String, Object> caseTrueData = caseMethod.getCaseTrueData();
        Map<String, Object> assertTrueData = caseMethod.getAssertTrueData();

        caseSteps.forEach(caseStep -> runPageObjectStructure(pageRunner, caseStep, caseTrueData));
        asserts.forEach(assertStep -> runAssert(pageRunner, assertStep, assertTrueData));
    }

    private static void runPageObjectStructure(PageRunner pageRunner,
                                               ICaseRunnable.PageObjectStructure poStructure,
                                               Map<String, Object> caseTrueData) {
        /**
         * 1、取出case传递给po方法的参数
         * 2、执行po方法
         */
        List<ICaseRunnable.ElementStructure> poSteps = poStructure.getPoSteps();
        List<String> params = poStructure.getParams();
        List<String> caseToPOParams = poStructure.getCaseToPOParams();
        String pageFileName = poStructure.getPageFileName();

        CaseInsensitiveMap<String, Object> poTrueData = new CaseInsensitiveMap<>();
        for (int i = 0; i < caseToPOParams.size(); ++i) {
            Object obj = caseTrueData.get(caseToPOParams.get(i));
            poTrueData.put(params.get(i), obj);
        }

        poSteps.forEach(poStep -> runPOStep(pageRunner, pageFileName, poStep, poTrueData));
    }

    private static void runPOStep(PageRunner pageRunner, String pageFileName,
                                  ICaseRunnable.ElementStructure poStep,
                                  Map<String, Object> poTrueData) {
        /**
         * 1、根据特定平台的定位符查找元素
         * 2、判断指定action所需的参数是否都有传递
         * 3、根据指定action操作元素
         */
        Map<String, ICaseRunnable.ElementSelector> selectors = poStep.getSelectors();
        String action = poStep.getAction();
        List<String> data = poStep.getData();


    }

    private static void runAssert(PageRunner pageRunner,
                                  ICaseRunnable.Assert caseAssert,
                                  Map<String, Object> assertTrueData) {
        /**
         * 执行assert
         */
    }
}
