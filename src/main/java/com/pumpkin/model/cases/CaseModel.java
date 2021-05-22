package com.pumpkin.model.cases;

import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

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
    private CaseInsensitiveMap<String, CaseMethodModel> cases;
}
