package com.senjay.archat.common.user.domain.vo.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FriendSearchResp {
    private Long id;
    private String username;
    private String avatar;
    private Integer exp;
    private LocalDateTime createTime;
    private Boolean status;



}
