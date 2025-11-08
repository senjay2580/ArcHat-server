package com.senjay.archat.common.chat.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberResp {
    @Schema(description = "uid")
    private Long uid;
    @Schema(description = "在线状态 1在线 2离线")
    private Integer activeStatus;
    /**
     * 角色权限
     */
    private Integer role;
}



