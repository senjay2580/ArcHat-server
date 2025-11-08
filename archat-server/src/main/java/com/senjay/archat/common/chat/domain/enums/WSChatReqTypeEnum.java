package com.senjay.archat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum WSChatReqTypeEnum {
    PRIVATE(1),

    GROUP(2),

    ERROR(-1),
    ;
    private final Integer type;

    //    可加缓存
    private static Map<Integer, WSChatReqTypeEnum> cache;

    static {
        cache = Arrays.stream(WSChatReqTypeEnum.values()).collect(Collectors.toMap(WSChatReqTypeEnum::getType, Function.identity()));
    }

    public static WSChatReqTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
