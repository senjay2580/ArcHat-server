package com.senjay.archat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FriendStatusEnum {
    PENDING(0,"待验证"),
    APPROVE(1,"通过"),
    REJECT(2,"拒绝")
    ;
    private final Integer code;
    private final String desc;
}
