package com.senjay.archat.common.chat.domain.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CreateGroupRoomReq {
    @NotBlank
    private String name;
    private String avatar;
    // 群公告
    private String groupDesc;
}
