package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

/**
 * @className: AppModel
 * @description: app-config.yaml文件中的app部分
 * @author: pumpkin
 * @date: 2021/5/20 10:31 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class AppModel {
    private CaseInsensitiveMap<String, Object> caps;
}
