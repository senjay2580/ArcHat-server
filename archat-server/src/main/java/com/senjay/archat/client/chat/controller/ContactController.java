package com.senjay.archat.client.chat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.senjay.archat.common.chat.domain.vo.request.BasePageReq;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupQueryResp;
import com.senjay.archat.common.chat.domain.vo.response.PrivateContactResp;
import com.senjay.archat.common.chat.domain.vo.response.UnreadMsgCntResp;
import com.senjay.archat.common.chat.service.ContactService;
import com.senjay.archat.common.user.domain.vo.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/client/contact")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @Operation(summary = "查询当前用户的私人会话列表")
    @PostMapping("/listPrivate")
    public Result<IPage<PrivateContactResp>> listPrivate(@Valid @RequestBody BasePageReq contactQueryReq) {
        return Result.success(contactService.listPrivate(contactQueryReq));
    }
    @Operation(summary = "查询当前用户的群聊会话列表")
    @PostMapping("/listGroup")
    public Result<IPage<GroupQueryResp>> listGroup(@Valid @RequestBody BasePageReq contactQueryReq) {
        return Result.success(contactService.listGroup(contactQueryReq));
    }
    @Operation(summary = "获取未读消息个数列表")
    @PostMapping("/unreadMsgCnt")
    public Result<IPage<UnreadMsgCntResp>> getUnreadMsgCnt(@Valid @RequestBody BasePageReq basePageReq) {
        return Result.success(contactService.getUnreadMsgCnt(basePageReq));
    }



}
