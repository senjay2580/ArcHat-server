package com.senjay.archat.common.constant;


public class MQConstant {

    private static final String PREFIX = "archat.";
//    项目前缀 -- 交换机
    public static final String PUSH_EXCHANGE = PREFIX + "push.exchange";
    public static final String MSG_EXCHANGE = PREFIX + "msg.exchange";
    public static final String UPDATE_EXCHANGE = PREFIX + "update.exchange";

    public static final String PUBLIC_PUSH_QUEUE = "public.push.queue";
    public static final String ONLINE_BINDING_KEY = "online";

    public static final String CHAT_MSG_QUEUE = "chat.msg.queue";
    public static final String CHAT_BINDING_KEY = "chat";

    public static final String CONTACT_QUEUE = "contact.queue";
    public static final String CONTACT_BINDING_KEY = "contact";

    public static final String  MEMBER_REMOVED_QUEUE = "member.removed.queue";
    public static final String GROUP_CLEAN_UP_BINDING_KEY = "group.cleanup";
    public static final String MEMBER_CLEAN_UP_BINDING_KEY = "member.cleanup";



}
