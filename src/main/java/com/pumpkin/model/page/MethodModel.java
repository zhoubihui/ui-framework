package com.pumpkin.model.page;

import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @className: MethodModel
 * @description: xxx-page.yaml中method的表达
 * @author: pumpkin
 * @date: 2021/5/21 8:59 上午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class MethodModel implements Model {
    //PO方法的形参
    private List<String> params;
    //PO方法的方法体
    private List<ElementModel> steps;
}
