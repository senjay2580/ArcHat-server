package com.senjay.archat.common.constant;
//<项目前缀>:<模块>:<功能序列>:   <标识符>

public class RedisConstant {
    public static final String LOGIN_USER_KEY = "archat:user:login:";
    public static final Long LOGIN_USER_TTL= 86400000L;
    public static final String GROUP_MEMBER_COUNT_KEY = "archat:group:member:count:";

//    过期时间 ：每个月下一个月的第一天00：00
    public static final String SIGNIN_USER_KEY = "archat:user:signin:";

//    UV 统计访客
    public static final String UV_USER_KEY = "archat:user:uv:";
//    AI 会话记忆Id
    public static final String AI_CHAT_MEMORY_KEY = "archat:aichat:memory:";
}
