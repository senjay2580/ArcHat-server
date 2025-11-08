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
public class FriendDTO {
    @NotNull
    private Long userId;
    @NotNull
    private Long friendId;
    @NotNull
    private Integer status;
}
