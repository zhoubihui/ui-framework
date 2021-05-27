package com.pumpkin.model.config;

import com.pumpkin.core.GlobalConfigParse;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @className: GlobalBaseModel
 * @description: global-config.yaml中的base部分描述
 * @author: pumpkin
 * @date: 2021/5/22 4:15 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class GlobalBaseModel {
    private String caseDirectory = "case/";
    private String dataDirectory = "data/";
    private String pageDirectory = "page/";
    private String selectorDirectory = "selector/";

    /**
     * 获取所属分组的相对路径
     * @return
     */
    public String getBaseDirectory(String fileName) {
        String baseDirectory = "";
        switch (fileName.split("-")[1].toUpperCase()) {
            case "CASE":
                baseDirectory = caseDirectory;
                break;
            case "DATA":
                baseDirectory = dataDirectory;
                break;
            case "PAGE":
                baseDirectory = pageDirectory;
                break;
            case "SELECTOR":
                baseDirectory = selectorDirectory;
                break;
        }
        return baseDirectory;
    }
}
