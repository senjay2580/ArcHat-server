package com.senjay.archat.common.chat.domain.enums;

import lombok.Getter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public enum WebRTCSignalTypeEnum {
    // 通话邀请
    CALL_INVITE("call-invite", "通话邀请"),
    // 接受通话
    CALL_ACCEPT("call-accept", "接受通话"),
    // 拒绝通话
    CALL_REJECT("call-reject", "拒绝通话"),
    // 挂断通话
    CALL_HANGUP("call-hangup", "挂断通话"),
    // SDP Offer
    OFFER("offer", "SDP Offer"),
    // SDP Answer
    ANSWER("answer", "SDP Answer"),
    // ICE候选
    ICE_CANDIDATE("ice-candidate", "ICE候选");

    private final String value;
    private final String description;

    // 使用 Map 缓存，提高查找效率
    private static final Map<String, WebRTCSignalTypeEnum> VALUE_MAP = new ConcurrentHashMap<>();

    // 静态代码块，在类加载时初始化 Map
    static {
        for (WebRTCSignalTypeEnum type : WebRTCSignalTypeEnum.values()) {
            VALUE_MAP.put(type.value, type);
        }
    }

    WebRTCSignalTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据 value 字符串查找对应的枚举项。
     * 使用 Map 缓存，查找效率为 O(1)。
     *
     * @param value 要查找的字符串值。
     * @return 匹配的 WebRTCSignalType 枚举项。
     * @throws IllegalArgumentException 如果找不到匹配的枚举项。
     */
    public static WebRTCSignalTypeEnum fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
        // 直接从 Map 中获取，效率更高
        WebRTCSignalTypeEnum type = VALUE_MAP.get(value);
        if (type == null) {
            throw new IllegalArgumentException("Unknown WebRTCSignalType value: " + value);
        }
        return type;
    }
}