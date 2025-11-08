package com.senjay.archat.common.chat.domain.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class BasePageReq {

    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;

}
