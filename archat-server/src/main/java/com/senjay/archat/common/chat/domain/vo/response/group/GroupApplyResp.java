package com.senjay.archat.common.chat.domain.vo.response.group;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupApplyResp {
    private Long id;
    private Long uid;
    private String username;
    private String avatar;
    private String msg;
    private Long roomId;
    private String name;
    private Integer status;
    private LocalDateTime updateTime;
}
