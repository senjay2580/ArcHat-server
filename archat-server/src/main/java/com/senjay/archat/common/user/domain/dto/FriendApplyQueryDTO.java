package com.senjay.archat.common.user.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class FriendApplyQueryDTO {
    @NotNull
    private Long uid;
    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;
}
