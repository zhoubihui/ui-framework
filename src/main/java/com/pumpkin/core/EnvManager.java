package com.pumpkin.core;

import com.pumpkin.runner.structure.Env;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @className: EnvManager
 * @description: env环境管理
 * @author: pumpkin
 * @date: 2021/5/26 9:53 下午
 * @version: 1.0
 **/
public class EnvManager {
    /**
     * 1、从当前case文件中获取配置文件信息，有则使用
     * 2、从当前case文件的直接父目录下获取配置文件信息(env-config.yaml)，有则使用
     * 3、从当前case文件的间接父目录下获取配置文件信息(env-config.yaml)，有则使用
     * 4、app-config.yaml/web-config.yaml/env-config.yaml文件中找默认选项(待确定)
     */
    private static EnvManager manager;
    /**
     * 1、当前case文件中定义的，不存储到Map，因为只能用于本文件
     * 234、存储到Map中，其中23存储时key是目录的名称?(或路径),4待确定
     */
    private Map<String, CaseInsensitiveMap<String, Object>> capsMap;

    private Map<String, CaseFileRecord> caseFileRecordMap;

    private EnvManager() {
        capsMap = new HashMap<>();
    }

    public static EnvManager getInstance() {
        synchronized (EnvManager.class) {
            if (Objects.isNull(manager))
                manager = new EnvManager();
        }
        return manager;
    }

    public CaseInsensitiveMap<String, Object> getCaps(String caseFileName, Env env) {
        /**
         * 如果是APP，这里返回的是具体的caps
         */
        CaseInsensitiveMap<String, Object> config = null;
        if (Objects.nonNull(env)) {
            config = PlatformConfigParse.getConfig(env);
        }
        /**
         * 234的步骤查找
         * 同样的问题：文件路径如何确定？其实只需要知道相对路径就可以了
         */
        return config;
    }

    /**
     * 从Map中移除env，如果是234情况，需要判断是否case文件都执行过了
     * @param caseFileName
     * @param env
     * @return
     */
    public boolean removeEnv(String caseFileName, Env env) {
        return false;
    }

    /**
     * caseFileNames: 存储执行过的case，用来确定目录下的case文件是否都执行完了
     * caseFileTotal: 目录下的case文件总数
     */
    class CaseFileRecord {
        private List<String> caseFileNames;
        private int caseFileTotal;
    }
}
