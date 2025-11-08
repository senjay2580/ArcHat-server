package com.senjay.archat.common.chat.domain.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class StatusConvertReq {
    @NotNull
    private Long roomId;
    @NotNull
    private Integer status;
}
