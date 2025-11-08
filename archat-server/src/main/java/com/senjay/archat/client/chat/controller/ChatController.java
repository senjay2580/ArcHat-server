package com.senjay.archat.client.chat.controller;

import com.senjay.archat.common.annotation.FrequencyControl;
import com.senjay.archat.common.chat.domain.vo.request.ChatMessageBaseReq;
import com.senjay.archat.common.chat.domain.vo.request.ChatMessageReq;
import com.senjay.archat.common.chat.domain.vo.response.group.ChatMessageResp;
import com.senjay.archat.common.chat.service.ChatService;
import com.senjay.archat.common.user.domain.vo.response.Result;
import com.senjay.archat.common.util.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

//    私聊使用ws协议通信，群聊使用http协议通信
    @Operation(summary = "群聊发送消息")
    @PostMapping("/msg")
    public Result<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq request) {
//        先获取消息id
        Long msgId = chatService.sendMsg(request, UserHolder.get().getId());
        //返回完整消息格式，方便前端展示
        return Result.success(chatService.getMsgResp(msgId));
    }

    @Operation(summary = "撤回消息")
    @PutMapping("/msg/recall")
    public Result<Void> recallMsg(@Valid @RequestBody ChatMessageBaseReq request) {
        chatService.recallMsg(UserHolder.get().getId(), request);
        return Result.success();
    }
}
