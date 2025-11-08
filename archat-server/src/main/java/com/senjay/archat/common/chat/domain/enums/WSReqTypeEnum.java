package com.senjay.archat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: ws前端请求类型枚举
 * Date: 2023-03-19
 */
// 整个 WebSocket 协议中前端发消息给后端的“指令类型”定义和解析中心。 command
@AllArgsConstructor
@Getter
public enum WSReqTypeEnum {
    LOGIN(1, "请求登录"),
    HEARTBEAT(2, "心跳包"),
    AUTHORIZE(3, "登录认证"),
    CHAT(4,"开始聊天"),
    WEBRTC_SIGNAL(6, "webrtc 信令")
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, WSReqTypeEnum> cache;

    static {
        cache = Arrays.stream(WSReqTypeEnum.values()).collect(Collectors.toMap(WSReqTypeEnum::getType, Function.identity()));
    }

    public static WSReqTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
