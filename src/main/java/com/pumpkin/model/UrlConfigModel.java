package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @className: UrlConfigModel
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/27 3:02 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class UrlConfigModel {
    private String pageUrl;
    private String selectorUrl;
    private String dataUrl;
}
