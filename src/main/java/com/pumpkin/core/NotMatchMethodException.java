package com.pumpkin.core;

/**
 * @className: NotMatchMethodException
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/23 10:19 上午
 * @version: 1.0
 **/
public class NotMatchMethodException extends RuntimeException {
    public NotMatchMethodException(String caseFileName, String methodName) {
        super(String.format("文件%s内找不到%s方法", caseFileName, methodName));
    }
}
