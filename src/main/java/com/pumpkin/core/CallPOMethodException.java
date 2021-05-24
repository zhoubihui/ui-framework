package com.pumpkin.core;

/**
 * @className: CallPOMethodException
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/24 9:36 下午
 * @version: 1.0
 **/
public class CallPOMethodException extends RuntimeException {
    public CallPOMethodException(String caseFileName, String caseName, String poFileName, String poMethod) {
        super(String.format("%s的%s测试方法调用%s的%sPO方法，实参和形参个数不对"));
    }
}
