package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @className: EnvModel
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/26 10:04 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class EnvModel {
    private String platform;
    private String targetApp;
}
