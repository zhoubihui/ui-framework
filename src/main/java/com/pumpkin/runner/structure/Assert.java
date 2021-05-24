package com.pumpkin.runner.structure;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hamcrest.Matchers;

import java.io.Serializable;

/**
 * @className: Assert
 * @description: 断言的结构
 * @author: pumpkin
 * @date: 2021/5/22 6:44 下午
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class Assert implements Serializable {
    private Matchers matcher;
    private Object expected;
    private Object actual;
}
