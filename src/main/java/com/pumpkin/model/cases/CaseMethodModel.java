package com.pumpkin.model.cases;

import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @className: CaseMethodModel
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/28 9:04 上午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class CaseMethodModel implements Model {
    private List<String> params;
    private List<String> steps;
    private List<CaseAssertModel> asserts;
}