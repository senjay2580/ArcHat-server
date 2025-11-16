package com.senjay.archat.common.user.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
// VO/Response （Res）
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResp {

    private Long uid;
    private String username;
    private String avatar;
    private Integer exp;
    private LocalDateTime createTime;
    private String token;
}
