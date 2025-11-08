package com.senjay.archat.common.user.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendSearchDTO {

    private Integer status;

    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;
}
