package com.senjay.archat.common.chat.domain.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HandleApplyReq {
    @NotNull
    private Long id;
    @NotNull
    private Integer status;
}

