package com.pumpkin.model.page;

import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @className: ElementModel
 * @description: xxx-page.yaml文件中关于element部分的表达
 * @author: pumpkin
 * @date: 2021/5/21 7:11 上午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class ElementModel implements Model {
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
