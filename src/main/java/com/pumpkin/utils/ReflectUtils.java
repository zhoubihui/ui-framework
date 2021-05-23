package com.pumpkin.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @className: ReflectUtils
 * @description: 反射工具类
 * @author: zhoubihui
 * @date: 2021/5/7 9:09 上午
 * @version: 1.0
 **/
public class ReflectUtils {

    /**
     * 根据方法名获取方法的Method实例，只找当前Class实例中的方法
     * 注意：
     * 1、这里因为不能调用JUnit5的反射工具类，所以就自己写一个；
     * @param clazz
     * @param methodName
     * @param argsType
     * @return
     */
    public static Optional<Method> findMethod(Class<?> clazz, String methodName, Class<?>... argsType) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, argsType);
        } catch (NoSuchMethodException e) {
            ExceptionUtils.throwAsUncheckedException(e);
        }
        return Optional.ofNullable(method);
    }

    /**
     * 调用指定的方法
     * @param method
     * @param target
     * @param args
     * @return
     */
    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return makeAccessible(method).invoke(target, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw ExceptionUtils.throwAsUncheckedException(getUnderlyingCause(e));
        }
    }

    /**
     * 设置当前方法是可访问的
     * @param method
     */
    private static Method makeAccessible(Method method) {
        method.setAccessible(true);
        return method;
    }

    /**
     * 获取目标方法最底层的异常，注意只有InvocationTargetException是目标方法引发异常
     * JUnit5中拷贝
     * @param t
     * @return
     */
    private static Throwable getUnderlyingCause(Throwable t) {
        if (t instanceof InvocationTargetException) {
            return getUnderlyingCause(((InvocationTargetException) t).getTargetException());
        }
        return t;
    }

    /**
     * 获取内部类名称
     * @param clazz
     * @return
     */
    public static String getInnerClassName(Class<?> clazz) {
        return clazz.getName().split("\\$")[1];
    }
}
