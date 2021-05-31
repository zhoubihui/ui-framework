package com.pumpkin.core;

import com.pumpkin.model.IModel;
import com.pumpkin.model.IPublic;
import com.pumpkin.runner.ICaseRunnable;
import com.pumpkin.utils.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

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
     * 2、从当前case文件的直接父目录下获取配置文件信息(env-config.yaml)，有则使用
     * 3、从当前case文件的间接父目录下获取配置文件信息(env-config.yaml)，有则使用
     * 4、app-config.yaml/web-config.yaml/env-config.yaml文件中找默认选项(待确定)
     */
    private static EnvManager manager;
    /**
     * 1、当前case文件中定义的，不存储到Map，因为只能用于本文件
     * 234、存储到Map中，其中23存储时key是目录的名称?(或路径),4待确定
     */
    private Map<String, ICaseRunnable.EnvConfig> capsMap;
    private final static String ENV_CONFIG = "env-config";

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

    public ICaseRunnable.EnvConfig getCaps(String caseFileName, ICaseRunnable.Env env) {
        ICaseRunnable.EnvConfig envConfig = null;
        if (Objects.nonNull(env)) {
            envConfig = PlatformConfigParse.getConfig(env);
            return envConfig;
        }
        /**
         * 234的步骤查找
         * 同样的问题：文件路径如何确定？其实只需要知道相对路径就可以了
         */
        String parentDirectory = caseFileName;
        String envFileName = "";
        while (StringUtils.isNotBlank((parentDirectory = FilenameUtils.getPathNoEndSeparator(parentDirectory)))) {
            envFileName = FileUtils.getFilePathFromDirectory(parentDirectory, ENV_CONFIG);
            if (StringUtils.isNotBlank(envFileName))
                break;
        }
        if (StringUtils.isNotBlank(envFileName)) {
            envFileName = FileUtils.getFilePathFromDirectory("", ENV_CONFIG);
            parentDirectory = "root";
        }
        envConfig = capsMap.get(parentDirectory);
        if (Objects.nonNull(envConfig))
            return envConfig;

        /**
         * 缓存中不存在则从文件中读取
         */
        IPublic.EnvModel envModel = IModel.getModel(envFileName, IPublic.EnvModel.class);
        ICaseRunnable.Env newEnv = ICaseRunnable.Env.builder().platform(envModel.getPlatform()).
                targetApp(envModel.getTargetApp()).build();
        envConfig = PlatformConfigParse.getConfig(newEnv);
        capsMap.put(parentDirectory, envConfig);
        return envConfig;
    }

    /**
     * 从Map中移除env，如果是234情况，需要判断是否case文件都执行过了
     * @param caseFileName
     * @param env
     * @return
     */
    public boolean removeEnv(String caseFileName, ICaseRunnable.Env env) {
        //怎么移除
        /**
         * 1、case文件内定义的env,因为不保存env所以此处直接返回true即可
         */
        if (Objects.nonNull(env))
            return true;
        return false;
    }
}
