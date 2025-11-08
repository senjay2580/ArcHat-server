package com.senjay.archat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum RoomStatusEnum {
    AVAILABLE(0,"正常"),
    FORBIDDEN(1,"禁用")

    ;
    private final Integer code;
    private final String desc;

}
