package com.pumpkin.model.page;

import com.pumpkin.model.UrlConfigModel;
import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

/**
 * @className: PageModel
 * @description: xxx-page.yaml文件的表达
 * @author: pumpkin
 * @date: 2021/5/21 7:10 上午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class PageModel implements Model {
    /**
     * 依赖文件的父目录
     */
    private UrlConfigModel config;

    private CaseInsensitiveMap<String, MethodModel> methods;

    /**
     * 获取指定方法名的数据
     * @param name
     * @return
     */
    public MethodModel getMethod(String name) {
        return methods.get(name);
    }
}
