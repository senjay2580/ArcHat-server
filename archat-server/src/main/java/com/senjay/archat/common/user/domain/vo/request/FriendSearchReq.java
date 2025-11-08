package com.senjay.archat.common.user.domain.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class FriendSearchReq {

    private String keyword;
    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;

}
