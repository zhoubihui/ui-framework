package com.pumpkin.core;

/**
 * @className: NotMatchParameterException
 * @description: yaml中的方法实参和方法定义的形参不匹配
 * @author: pumpkin
 * @date: 2021/5/23 8:03 上午
 * @version: 1.0
 **/
public class NotMatchParameterException extends RuntimeException {
    public NotMatchParameterException(String methodName, String param) {
        super(String.format("方法%s的实参与形参%s不匹配", methodName, param));
    }
}
