package com.senjay.archat.common.chat.domain.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AdminCreatePrivateRoomReq {
    @NotNull
    private Long uid1;
    @NotNull
    private Long uid2;

}
