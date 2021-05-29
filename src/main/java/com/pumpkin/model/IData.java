package com.pumpkin.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.List;
import java.util.Map;

/**
 * @className: IDataModel
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/30
 * @version: 1.0
 **/
public interface IData {
    @Data
    @Accessors(chain = true)
    class DataModel implements IModel {
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
}
