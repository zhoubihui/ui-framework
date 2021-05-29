package com.pumpkin.runner.structure;

import com.pumpkin.runner.CaseRunnable;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

/**
 * @className: EnvAndCaps
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/29
 * @version: 1.0
 **/
@Data
@Builder
public class EnvAndCaps {
    private CaseRunnable.Env env;
    private CaseInsensitiveMap<String, Object> caps;
}
