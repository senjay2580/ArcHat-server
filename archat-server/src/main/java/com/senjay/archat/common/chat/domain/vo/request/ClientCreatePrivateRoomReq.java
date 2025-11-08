package com.senjay.archat.common.chat.domain.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ClientCreatePrivateRoomReq {

    @NotNull
    private Long uid;
}
