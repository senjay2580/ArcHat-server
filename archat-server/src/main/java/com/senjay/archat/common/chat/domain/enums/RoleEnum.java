package com.senjay.archat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 角色枚举
 */
@AllArgsConstructor
@Getter
public enum RoleEnum {
    GROUP_OWNER(1, "群主"),
    ADMIN(2, "群聊管理"),
    MEMBER(3,"群成员")
    ;

    private final Integer code;
    private final String desc;
    private static Map<Integer, RoleEnum> cache;

    static {
        cache = Arrays.stream(RoleEnum.values()).collect(Collectors.toMap(RoleEnum::getCode, Function.identity()));
    }
    public static RoleEnum of(Integer code) {
        return cache.get(code);
    }
}
