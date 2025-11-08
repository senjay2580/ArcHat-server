package com.senjay.archat.common.util;


import com.senjay.archat.common.user.domain.vo.request.RequestInfo;
// get set remove 以及静态方法
public class UserHolder {
    private static final ThreadLocal<RequestInfo> threadLocal = new ThreadLocal<>();
    public static void set(RequestInfo info) {
        threadLocal.set(info);
    }
    public static RequestInfo get() {
        return threadLocal.get();
    }
    public static void remove() {
        threadLocal.remove();
    }


}
