package com.pumpkin.model.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @className: GlobalConfigModel
 * @description: global-config.yaml的描述
 * @author: pumpkin
 * @date: 2021/5/22 4:13 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class GlobalConfigModel {
    private GlobalBaseModel base;
}
