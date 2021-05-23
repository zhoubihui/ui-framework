package com.pumpkin.utils;

/**
 * @className: ExceptionUtils
 * @description: 异常处理工具类
 * @author: pumpkin
 * @date: 2021/5/7 9:43 下午
 * @version: 1.0
 **/
public class ExceptionUtils {

    /**
     * 抛出一个运行时异常
     * JUnit5中拷贝
     * @param t
     * @returnØ
     */
    public static RuntimeException throwAsUncheckedException(Throwable t) {
        ExceptionUtils.throwAs(t);
        //永远不会执行到这里
        return null;
    }

    /**
     * JUnit5中拷贝
     * @param t
     * @param <T>
     * @throws T
     */
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwAs(Throwable t) throws T {
        throw (T) t;
    }
}
