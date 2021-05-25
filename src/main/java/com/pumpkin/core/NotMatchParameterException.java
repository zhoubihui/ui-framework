package com.pumpkin.core;

/**
 * @className: NotMatchParameterException
 * @description: yaml中的方法实参和方法定义的形参不匹配
 * @author: pumpkin
 * @date: 2021/5/23 8:03 上午
 * @version: 1.0
 **/
public class NotMatchParameterException extends RuntimeException {
    public NotMatchParameterException(String fileName, String methodName) {
        super(String.format("文件%s内的方法%s内引用的参数在params部分没有定义", fileName, methodName));
    }
}
