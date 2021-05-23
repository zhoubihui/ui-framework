package com.pumpkin.runner;

import com.pumpkin.model.Model;
import com.pumpkin.model.cases.CaseModel;

/**
 * @className: CaseManager
 * @description: case管理器
 * @author: pumpkin
 * @date: 2021/5/22 11:24 上午
 * @version: 1.0
 **/
public class CaseManager {
    private static CaseManager caseManager;
    private CaseManager() {
    }

    public static CaseManager getInstance() {
        synchronized (CaseManager.class) {
            if (caseManager == null)
                caseManager = new CaseManager();
        }
        return caseManager;
    }

    /**
     * 根据用例名称，获取对应的用例结构实例
     * @param caseFileName 举例：search-case
     * @return
     */
    public CaseRunnable getCase(String caseFileName) {
        CaseParse caseParse = CaseParse.builder().caseFileName(caseFileName).build();
        //1、先从缓存中找，缓存中有即返回
        //2、缓存中没有，从硬盘读取对应文件并解析
        /**
         * 怎么确定文件路径
         * 1、需要在global-config.yaml中指定case的绝对路径
         * 2、通过commons-io包
         */
        CaseModel caseModel = Model.getModel(caseFileName, CaseModel.class);
        CaseRunnable caseRunnable = caseParse.parseCase(caseModel);
        /**
         * 1、放入缓存
         * 2、CaseRunner执行CaseRunnable
         * 3、问题：应该怎么去往CaseRunner
         */
        //CASE_RUNNER.runCase(caseRunnable);
        return caseRunnable;
    }
}
