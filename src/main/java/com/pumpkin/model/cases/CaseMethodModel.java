package com.pumpkin.model.cases;

import com.pumpkin.model.Model;
import com.pumpkin.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;

/**
 * @className: CaseMethodModel
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/21 10:24 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class CaseMethodModel implements Model {
    private final String PARAM_REGEX = "\\((.+?)\\)";
    private final String PARAM_SPLIT_REGEX = "\\{(.+?)\\}.+\\{(.+?)\\}";

    private List<String> params;
    private List<String> steps;
    private List<CaseAssertModel> asserts;

    /**
     * 判断steps引入的参数是否都存在params中
     * @return
     */
    public boolean isTrueParams() {
        return steps.stream().allMatch(
                s -> {
                    String param = StringUtils.matcher(s, PARAM_REGEX);
                    if (org.apache.commons.lang3.StringUtils.isBlank(param))
                        return true;
                    //param可能的格式: ${keyword},${replace},再通过正则表达式进一步提取
                    return params.containsAll(StringUtils.matchers(param, PARAM_SPLIT_REGEX));
                }
        );
    }
}
