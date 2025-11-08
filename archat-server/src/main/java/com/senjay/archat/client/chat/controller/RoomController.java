package com.senjay.archat.client.chat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.senjay.archat.common.chat.domain.dto.GroupApplyDTO;
import com.senjay.archat.common.chat.domain.dto.GroupModifyDTO;
import com.senjay.archat.common.chat.domain.vo.request.*;
import com.senjay.archat.common.chat.domain.vo.response.PrivateContactResp;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupApplyResp;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupDetailResp;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupMemberSimpleResp;
import com.senjay.archat.common.chat.domain.vo.response.group.MyApplyResp;
import com.senjay.archat.common.chat.service.RoomService;
import com.senjay.archat.common.user.domain.vo.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController("clientRoomController")
@RequestMapping("/client/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @Operation(summary = "添加私聊房间")
    @PostMapping("/addPrivate")
    public Result addPrivateRoom(@Valid @RequestBody ClientCreatePrivateRoomReq clientCreatePrivateRoomReq) {
        roomService.addPrivateRoom(clientCreatePrivateRoomReq);
        return Result.success();
    }

    @Operation(summary = "切换房间状态")
    @PostMapping("/convertStatus")
    public Result convertStatus(@Valid @RequestBody StatusConvertReq statusConvertReq) {
        roomService.convertStatus(statusConvertReq);
        return Result.success();
    }

    @Operation(summary = "查询房间是否存在")
    @GetMapping("/checkPrivateRoom")
    public Result<Boolean> checkPrivateRoom(Long fid) {
        return Result.success(roomService.checkPrivateRoom(fid));
    }


    @Operation(summary = "创建群聊房间")
    @PostMapping("/addGroup")
    public Result addGroupRoom(@Valid @RequestBody CreateGroupRoomReq createGroupRoomReq) {
        roomService.addGroupRoom(createGroupRoomReq);
        return Result.success();
    }

    @Operation(summary = "搜索群聊")
    @PostMapping("/group/search")
    public Result<IPage<GroupDetailResp>> searchGroup(
            @RequestBody BasePageReq basePageReq,
            @NotBlank @RequestParam String keyword

    ) {
        return Result.success(roomService.searchGroup(basePageReq, keyword));
    }

    @Operation(summary = "查询群聊详情信息列表")
    @PostMapping("/groupDetail")
    public Result<GroupDetailResp> getGroupDetail(
            @RequestParam @NotNull @Min(1) Long roomId// 基本类型参数
    ) {
        return Result.success(roomService.getGroupDetail(roomId));
    }

    @Operation(summary = "查询群聊的成员")
    @PostMapping("/list/groupMember")
    public Result<IPage<GroupMemberSimpleResp>> listGroupMember(
            @RequestParam @NotNull @Min(1) Long roomId,
            @RequestBody BasePageReq basePageReq

    ) {
        return Result.success(roomService.listGroupMember(roomId, basePageReq));
    }

    @Operation(summary = "查询当前群聊的总人数")
    @PostMapping("/group/memberCount")
    public Result<Long> getGroupMemberCount(@RequestParam @NotNull @Min(1) Long roomId) {
        return Result.success(roomService.countMember(roomId));
    }


    @Operation(summary = "发送加入群聊申请")
    @PostMapping("/apply/joinGroup")
    public Result applyJoin(@Valid @RequestBody GroupApplyDTO groupApplyDTO) {
        roomService.applyJoinGroup(groupApplyDTO);
        return Result.success();
    }

    @Operation(summary = "修改群聊申请留言")
    @PostMapping("/apply/modifyMsg")
    public Result modifyApplyMsg(@NotNull @RequestBody ModifyApplyMsgReq modifyApplyMsgReqs) {
        roomService.modifyApplyMsg(modifyApplyMsgReqs);
        return Result.success();
    }

    @Operation(summary = "查询其他用户对自己群聊的申请")
    @PostMapping("/list/otherApply")
    public Result<IPage<GroupApplyResp>> listJoinApply(@RequestBody BasePageReq basePageReq) {
        return Result.success(roomService.listJoinApply(basePageReq));
    }

    @Operation(summary = "查询自己申请群聊的记录")
    @PostMapping("/list/myApply")
    public Result<IPage<MyApplyResp>> listMyApply(@RequestBody BasePageReq basePageReq) {
        return Result.success(roomService.listMyApply(basePageReq));
    }

    @Operation(summary = "群主处理加入群聊的申请")
    @PostMapping("/apply/dealWith")
    public Result applyDealWith(@Valid @RequestBody HandleApplyReq handleApplyReq) {
        roomService.applyDealWith(handleApplyReq);
        return Result.success();
    }

    @Operation(summary = "邀请成员加入群聊")
    @PostMapping("/group/invite")
    public Result inviteToBeMember(@Valid @RequestBody UserAndRoomReq userAndRoomReq) {
        roomService.inviteToBeMember(userAndRoomReq);
        return Result.success();
    }


    @Operation(summary = "解散群聊")
    @DeleteMapping("/group/{roomId}")
    public Result dismissGroup(@PathVariable("roomId") Long roomId) {
        roomService.dismissGroup(roomId);
        return Result.success();
    }




    @Operation(summary = "将成员踢出群聊")
    @PostMapping("/removeMember")
    public Result removeMember(@Valid @RequestBody UserAndRoomReq removeMemberReq) {
        roomService.removeMember(removeMemberReq);
        return Result.success();
    }


    @Operation(summary = "用户退出群聊")
    @GetMapping("/outGroup/{roomId}")
    public Result outGroup(@NotNull @PathVariable Long roomId ) {
        roomService.outGroup(roomId);
        return Result.success();
    }

    @Operation(summary = "修改群聊信息")
    @PostMapping("/group/modify")
    public Result modifyGroup(@Valid @RequestBody GroupModifyDTO groupModifyDTO) {
        roomService.modifyGroup(groupModifyDTO);
        return Result.success();
    }
    // TODO
    @Operation(summary = "群主撤销/授予群成员管理权限")
    @PostMapping("/auth")
    public Result authorizeMember(@Valid @RequestBody HandleAuthReq handleAuthReq) {
        roomService.authorizeMember(handleAuthReq);
        return Result.success();
    }

}
