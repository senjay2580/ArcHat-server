package com.senjay.archat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoomApplyStatusEnum {
    PENDING(0,"待管理员审批"),
    ADMIT(1,"管理员通过"),
    REJECT(2,"管理员拒绝")
    ;
    private final Integer code;
    private final String desc;
}
