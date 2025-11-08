package com.senjay.archat.common.chat.domain.vo.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResp {
    private Long id;
    private Long fromUid;
    private String content;
    private Integer status;
    private Integer type;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
