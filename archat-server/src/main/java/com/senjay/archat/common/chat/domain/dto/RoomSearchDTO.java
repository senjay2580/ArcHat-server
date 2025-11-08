package com.senjay.archat.common.chat.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomSearchDTO {
    private Long uid1;
    private Long uid2;
    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;
    private Integer status;
}
