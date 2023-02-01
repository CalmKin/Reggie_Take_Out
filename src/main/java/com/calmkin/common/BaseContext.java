package com.calmkin.common;

/**
 * 基于ThreadLocal封装的工具类，用户保存和获取当前id
 */
public class BaseContext {
    public  static ThreadLocal<Long> ID = new ThreadLocal<>();
    public static void setID(Long id)
    {
        ID.set(id);
    }

    public static Long getID()
    {
        return ID.get();    //获取这个变量的值
    }

}
