package com.senjay.archat.common.chat.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class GroupApplyDTO {
    @NotNull
    private Long roomId;
    @Size(max = 50)
    private String msg;
}
