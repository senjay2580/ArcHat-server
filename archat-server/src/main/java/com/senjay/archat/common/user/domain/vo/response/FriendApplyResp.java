package com.senjay.archat.common.user.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendApplyResp {
    private Long id;
    private Long friendId;
    private String username;
    private String avatar;
    private Integer exp;
    private LocalDateTime createTime;
    // 申请情况
    private Integer applyStatus;
    // 用户在线情况
    private Boolean userStatus;
}
