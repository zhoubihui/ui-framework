package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.List;

/**
 * @className: IPageModel
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/30
 * @version: 1.0
 **/
public interface IPage {
    @Data
    @Accessors(chain = true)
    class PageModel implements IModel {
        /**
         * 依赖文件的父目录
         */
        private IPublic.UrlConfigModel config;

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

    @Data
    @Accessors(chain = true)
    class MethodModel {
        //PO方法的形参
        private List<String> params;
        //PO方法的方法体
        private List<ElementModel> steps;
    }

    @Data
    @Accessors(chain = true)
    class ElementModel {
        /**
         * 读取进来的是占位符，需要做替换
         */
        private String selector;
        /**
         * click: 表示元素的click操作
         * input: 表示元素的sendKeys操作，这个需要参数配合
         * get: 表示元素的getAttribute操作，需要参数配合
         */
        private String action;
        /**
         * 第一个元素: action是send、get时需要输入的参数
         * 第二个元素: action是input时，本次输入是否先清空之前的输入
         */
        private List<String> data;
    }
}
