package com.pumpkin.runner;

import com.pumpkin.core.PageManager;
import com.pumpkin.core.PageHelper;
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
         * 4、初始化PageRunner，内部初始化driver
         * 4、执行@BeforeAll
         * 5、执行@BeforeEach
         * 6、执行case
         * 7、执行@AfterEach
         * 8、执行@AfterAll
         * 9、移除环境变量、driver等
         *  注意：345是循环执行
         */
        List<ICaseRunnable.CaseStructure> cases = caseRunnable.getCases();
        ICaseRunnable.Env env = caseRunnable.getEnv();
        String caseFileName = caseRunnable.getCaseFileName();
        if (cases.isEmpty())
            return;
        PageHelper pageHelper = PageManager.getInstance().getPageHelper(caseFileName, env);

        cases.forEach(caseStructure -> runCaseStructure(pageHelper, caseStructure));

        PageManager.getInstance().removePageHelper(caseFileName, env, pageHelper.getEnvConfig());
    }

    private static void runCaseStructure(PageHelper pageHelper, ICaseRunnable.CaseStructure caseStructure) {
        /**
         * 1、执行@BeforeEach
         * 2、执行case
         * 3、执行@AfterEach
         */
        List<ICaseRunnable.CaseMethod> cases = caseStructure.getCases();
        cases.forEach(caseMethod -> runCaseMethod(pageHelper, caseMethod));
    }

    private static void runCaseMethod(PageHelper pageHelper, ICaseRunnable.CaseMethod caseMethod) {
        /**
         * 1、执行caseSteps
         * 2、执行asserts
         */
        List<ICaseRunnable.PageObjectStructure> caseSteps = caseMethod.getCaseSteps();
        List<ICaseRunnable.Assert> asserts = caseMethod.getAsserts();
        Map<String, Object> caseTrueData = caseMethod.getCaseTrueData();
        Map<String, Object> assertTrueData = caseMethod.getAssertTrueData();

        caseSteps.forEach(caseStep -> runPageObjectStructure(pageHelper, caseStep, caseTrueData));
        asserts.forEach(assertStep -> runAssert(pageHelper, assertStep, assertTrueData));
    }

    private static void runPageObjectStructure(PageHelper pageHelper,
                                               ICaseRunnable.PageObjectStructure poStructure,
                                               Map<String, Object> caseTrueData) {
        /**
         * 1、取出case传递给po方法的参数
         * 2、执行po方法
         *      1) 根据特定平台的定位符查找元素
         *      2) 判断指定action所需的参数是否都有传递
         *      3) 根据指定action操作元素
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

        poSteps.forEach(poStep -> pageHelper.runCase(pageFileName, poStep, poTrueData));
    }

    private static void runAssert(PageHelper pageHelper,
                                  ICaseRunnable.Assert caseAssert,
                                  Map<String, Object> assertTrueData) {
        /**
         * 执行assert
         */

    }
}
