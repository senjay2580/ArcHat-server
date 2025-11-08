package com.senjay.archat.common.user.domain.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyDealWithReq {
    @NotNull
    private Long friendId;
    @NotNull
    private Integer status;
}
