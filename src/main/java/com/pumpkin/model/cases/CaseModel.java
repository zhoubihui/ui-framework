package com.pumpkin.model.cases;

import com.pumpkin.model.UrlConfigModel;
import com.pumpkin.model.EnvModel;
import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.List;

/**
 * @className: CaseModel
 * @description: xxx-case.yaml的描述
 * @author: pumpkin
 * @date: 2021/5/21 10:23 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class CaseModel implements Model {
    /**
     * 定义这个case文件运行的环境
     */
    private EnvModel env;

    /**
     * 定义依赖文件路径
     */
    private UrlConfigModel config;

    /**
     * 测试方法
     */
    private List<CaseInsensitiveMap<String, CaseMethodModel>> cases;
}
