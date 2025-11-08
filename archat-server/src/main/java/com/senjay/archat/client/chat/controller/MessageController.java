package com.senjay.archat.client.chat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.senjay.archat.common.chat.domain.vo.request.BasePageReq;
import com.senjay.archat.common.chat.domain.vo.request.GroupMsgQueryReq;
import com.senjay.archat.common.chat.domain.vo.request.MessageQueryReq;
import com.senjay.archat.common.chat.domain.vo.response.MessageResp;
import com.senjay.archat.common.chat.domain.vo.response.group.ChatMessageResp;
import com.senjay.archat.common.chat.service.MessageService;
import com.senjay.archat.common.user.domain.vo.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client/message")
@RequiredArgsConstructor
public class MessageController {
    private final  MessageService messageService;

    @Operation(summary = "查询和当前用户的聊天记录")
    @PostMapping("/friend/list")
    public Result<List<MessageResp>> listFriendMessage(@Valid @RequestBody MessageQueryReq messageQueryReq) {
        return Result.success(messageService.listMessage(messageQueryReq));
    }

    @Operation(summary = "查询信息通过id")
    @GetMapping("/{id}")
    public Result<MessageResp> getMessage(@PathVariable Long id) {
        return Result.success(messageService.getMessage(id));
    }

    @Operation(summary = "查询当前群聊房间的消息")
    @PostMapping("/group/list")
    public Result<IPage<ChatMessageResp>> listGroupMessage(@Valid @RequestBody GroupMsgQueryReq groupMsgQueryReq) {
        return Result.success(messageService.listGroupMessage(groupMsgQueryReq));
    }
}
