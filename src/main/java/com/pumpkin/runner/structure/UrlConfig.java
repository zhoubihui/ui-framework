package com.pumpkin.runner.structure;

import lombok.Builder;
import lombok.Data;

/**
 * @className: UrlConfig
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/27 2:59 下午
 * @version: 1.0
 **/
@Data
@Builder
public class UrlConfig {
    private String pageUrl;
    private String selectorUrl;
    private String dataUrl;
}
