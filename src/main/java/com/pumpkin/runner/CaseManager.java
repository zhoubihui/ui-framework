package com.pumpkin.runner;

import com.pumpkin.model.ICase;
import com.pumpkin.model.IModel;

/**
 * @className: CaseManager
 * @description: case管理器
 * @author: pumpkin
 * @date: 2021/5/22 11:24 上午
 * @version: 1.0
 **/
public class CaseManager {
    private static CaseManager manager;
    private CaseManager() {
    }

    public static CaseManager getInstance() {
        synchronized (CaseManager.class) {
            if (manager == null)
                manager = new CaseManager();
        }
        return manager;
    }

    /**
     * 根据用例名称，获取对应的用例结构实例
     * 运行用例的可能性：
     * 1、单个case文件运行
     * 2、运行目录下的case文件
     * 所以传入这个方法的caseFileName是一个相对路径，指定要运行哪个case文件，2是上层处理的
     * @param caseFileName 举例：case/search-case.yaml
     * @return
     */
    public CaseRunnable getCase(String caseFileName) {
        CaseParse caseParse = new CaseParse();
        //1、先从缓存中找，缓存中有即返回
        //2、缓存中没有，从硬盘读取对应文件并解析
        /**
         * 怎么确定文件路径
         * 1、需要在global-config.yaml中指定case的绝对路径
         * 2、通过commons-io包
         */
        ICase.CaseModel caseModel = IModel.getModel(caseFileName, ICase.CaseModel.class);
        CaseRunnable caseRunnable = caseParse.parseCase(caseFileName, caseModel);
        /**
         * 1、放入缓存
         * 2、CaseRunner执行CaseRunnable
         * 3、问题：应该怎么去往CaseRunner
         */
        //CASE_RUNNER.runCase(caseRunnable);
        return caseRunnable;
    }
}
