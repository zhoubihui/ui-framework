package com.pumpkin.runner.structure;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @className: Env
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/26 10:07 下午
 * @version: 1.0
 **/
@Data
@Builder
public class Env implements Serializable {
    private String platform;
    private String targetApp;
}
