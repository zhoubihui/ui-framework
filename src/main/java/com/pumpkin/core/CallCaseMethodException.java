package com.pumpkin.core;

/**
 * @className: CallCaseMethodException
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/25 11:12 上午
 * @version: 1.0
 **/
public class CallCaseMethodException extends RuntimeException {
    public CallCaseMethodException(String caseFileName, String caseMethodName,
                                   String dataFileName) {
        super(String.format("文件%s的%s方法内定义的变量在%s数据文件中没有找到", caseFileName, caseMethodName, dataFileName));
    }
}
