package com.senjay.archat.common.chat.domain.vo.response.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResp {

    private UserInfo fromUser;

    @Schema(description = "消息详情")
    private Message message;

    @Data
    public static class UserInfo {
        private Long uid;
    }


    @Data
    public static class Message {
        @Schema(description = "消息id")
        private Long id;

        @Schema(description = "消息来自的房间id")
        private Long roomId;

        @Schema(description = "消息发送时间 也就是消息创建/更新时间")
        private LocalDateTime sendTime;

        @Schema(description = "消息类型 1正常文本 2.撤回消息 以及各种多模态信息 ")
        private Integer type;

        @Schema(description = "消息内容，不同的消息类型，内容体不同")
        private Object body;


    }


}
