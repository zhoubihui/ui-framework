package com.pumpkin.core;

import com.pumpkin.runner.structure.Env;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.HashMap;
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
     * 2、从当前case文件的直接父目录下获取配置文件信息，有则使用
     * 3、从当前case文件的间接父目录下获取配置文件信息，有则使用
     * 4、app-config.yaml文件中找默认选项
     */
    private static EnvManager manager;
    private Map<String, CaseInsensitiveMap<String, Object>> capsMap = null;

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
            config = PlatformConfigParse.getConfig(env.getPlatform(), env.getTargetApp());
        }
        return config;
    }
}
