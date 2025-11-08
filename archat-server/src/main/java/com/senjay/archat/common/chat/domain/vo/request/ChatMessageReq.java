package com.senjay.archat.common.chat.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 聊天信息点播
 * Description: 消息发送请求体

 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReq {
    @NotNull
    @Schema(description ="房间id")
    private Long roomId;

    @Schema(description = "消息类型")
    @NotNull
    private Integer msgType;

//    多模态消息
    @Schema(description ="消息内容，类型不同传值不同")
    @NotNull
//    前后端约定传值json格式 使用object 统一接收
    private Object body;

}
