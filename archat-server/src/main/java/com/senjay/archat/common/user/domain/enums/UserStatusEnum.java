package com.senjay.archat.common.user.domain.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum UserStatusEnum {
    ONLINE(1,"在线"),
    OFFLINE(0,"离线")
    ;
    private final Integer code;
    private final String desc;


}
