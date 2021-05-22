package com.pumpkin.model.data;

import com.pumpkin.model.Model;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.List;
import java.util.Map;

/**
 * @className: DataModel
 * @description: xxx-data.yaml文件的描述
 * @author: pumpkin
 * @date: 2021/5/21 9:59 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class DataModel implements Model {
    private CaseInsensitiveMap<String, CaseInsensitiveMap<String, List<Object>>> data;

    /**
     * 获取指定方法名的全部参数
     * @param name
     * @return
     */
    public Map<String, List<Object>> getMethodData(String name) {
        return data.get(name);
    }
}
