package com.senjay.archat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.annotations.Param;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum WSChatRespTypeEnum {
    TEXT(1000),
    APPLYTIPS(1001),
    ;
    private final Integer type;

    private static Map<Integer, WSChatRespTypeEnum> cache;
    static {
        cache = Arrays.stream(WSChatRespTypeEnum.values()).collect(Collectors.toMap(WSChatRespTypeEnum::getType, Function.identity()));
    }
    public static WSChatRespTypeEnum of(Integer type) {return cache.get(type);}
}
