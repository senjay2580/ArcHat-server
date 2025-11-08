package com.senjay.archat.common.chat.domain.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class HandleAuthReq {
    @NotNull
    private Long uid;
    @NotNull
    private Long groupId;
//    撤权/授权
    @NotNull
    private Integer role;
}
