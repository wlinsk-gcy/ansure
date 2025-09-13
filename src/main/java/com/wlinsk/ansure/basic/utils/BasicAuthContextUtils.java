package com.wlinsk.ansure.basic.utils;

/**
 * @Author: wlinsk
 * @Date: 2025/7/6
 */
public class BasicAuthContextUtils {
    private final static ThreadLocal<String> LOGIN_INFO = new ThreadLocal<>();
    public static void init(String userId){
        LOGIN_INFO.remove();
        LOGIN_INFO.set(userId);
    }
    public static void clearContext(){
        String userId = LOGIN_INFO.get();
        userId = null;
        LOGIN_INFO.remove();
    }

    public static String getUserId(){
        return LOGIN_INFO.get();
    }
}
