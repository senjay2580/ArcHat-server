package com.senjay.archat.common.chat.domain.vo.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PrivateContactResp {
    // 会话id
    private Long id;
    // 房间id
    private Long roomId;
    // 聊天对方
    private Long friendId;
    // 会话内消息最后更新的时间 按照这个排序
    private LocalDateTime activeTime;
    // 会话最新消息
    private Long lastMsgId;
    // 阅读到的最后时间
    private LocalDateTime readTime;

}
