package com.pumpkin.exception;

/**
 * @className: NotMatchActionException
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/31 4:03 下午
 * @version: 1.0
 **/
public class NotMatchActionException extends RuntimeException {
    public NotMatchActionException(String pageFileName, String action) {
        super(String.format("PO文件%s有不支持的action: %s", pageFileName, action));
    }
}
