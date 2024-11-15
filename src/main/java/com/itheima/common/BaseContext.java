package com.itheima.common;

//基于ThreadLocal封装的工具类，用于设置和保存当前登录用户的id
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentThread(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentThread() {
        return threadLocal.get();
    }
}
