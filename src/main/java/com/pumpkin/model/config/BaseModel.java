package com.pumpkin.model.config;

import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

/**
 * @className: BaseModel
 * @description: app-config的base部分
 * @author: pumpkin
 * @date: 2021/5/20 9:59 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class BaseModel implements Model {
    private CaseInsensitiveMap<String, Object> config;
    private CaseInsensitiveMap<String, Object> caps;

    /**
     * 获取指定的capability值
     * @param capabilityName
     * @return
     */
    public Object getCapability(String capabilityName) {
        return caps.get(capabilityName);
    }
}
