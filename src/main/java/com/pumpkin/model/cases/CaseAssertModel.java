package com.pumpkin.model.cases;

import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @className: CaseAssertModel
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/21 10:35 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class CaseAssertModel implements Model {
    private String type;
    private String expected;
    private String actual;
}
