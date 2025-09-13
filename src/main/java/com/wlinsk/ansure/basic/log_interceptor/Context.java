package com.wlinsk.ansure.basic.log_interceptor;

/**
 * @Author: wlinsk
 * @Date: 2024/5/22
 */
public class Context {
    private static final ThreadLocal<Request> REQUEST_LOCAL = new InheritableThreadLocal<>();

    private static final ThreadLocal<Object> RESPONSE = new InheritableThreadLocal<>();

    public static void setResponse(Object data) {
        RESPONSE.set(data);
    }
    public static Object getResponse() {
        return RESPONSE.get();
    }

    public static Request getCurrentRequest() {
        return REQUEST_LOCAL.get();
    }


    public final static void initContext(Request request){
        REQUEST_LOCAL.set(request);
    }

    public static void clearContext(){
        Request request = REQUEST_LOCAL.get();
        request = null;
        REQUEST_LOCAL.remove();

        Object data = RESPONSE.get();
        data = null;
        RESPONSE.remove();
    }
}
