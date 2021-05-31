package com.pumpkin.exception;

/**
 * @className: NotMatchPlatformException
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/5/31
 * @version: 1.0
 **/
public class NotMatchPlatformException extends RuntimeException {
    public NotMatchPlatformException(String envStr) {
        super("%s配置的platform错误");
    }
}
