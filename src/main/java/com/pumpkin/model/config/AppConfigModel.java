package com.pumpkin.model.config;

import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.Map;
import java.util.Objects;

/**
 * @className: AppConfigModel
 * @description: app-config.yaml的模型类
 * @author: pumpkin
 * @date: 2021/5/20 9:59 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class AppConfigModel implements Model {
    //存储基本配置信息
    private BaseModel base;

    //存储app相关配置信息，使用不区分大小写的Map
    private CaseInsensitiveMap<String, CaseInsensitiveMap<String, AppModel>> app;

    /**
     * 返回指定平台下的全部app设置信息
     * @param platform Android，或iOS，不区分大小写
     * @return
     */
    public Map<String, AppModel> getPlatformDetail(String platform) {
        return app.get(platform);
    }

    /**
     * 获取指定平台，指定app下的全部设置信息，不区分大小写
     * @param platform
     * @param appKey
     * @return
     */
    public AppModel getAppDetail(String platform, String appKey) {
        Map<String, AppModel> platformDetail = getPlatformDetail(platform);
        if (Objects.isNull(platformDetail))
            return null;
        return platformDetail.get(appKey);
    }


}
