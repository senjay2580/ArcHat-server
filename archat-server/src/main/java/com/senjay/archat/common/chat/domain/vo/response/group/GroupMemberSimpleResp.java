package com.senjay.archat.common.chat.domain.vo.response.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class GroupMemberSimpleResp {
    private Long uid;
    private String username;
    private String avatar;
    // 1=管理员，2=群主
    private Integer role;
    private Integer status;
    private String exep;
//    updateTime group_member
    private LocalDateTime joinGroupTime;
}
