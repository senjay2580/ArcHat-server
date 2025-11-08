package com.senjay.archat.admin.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.chat.domain.dto.RoomSearchDTO;
import com.senjay.archat.common.chat.domain.entity.RoomFriend;
import com.senjay.archat.common.chat.domain.vo.request.AdminCreatePrivateRoomReq;
import com.senjay.archat.common.chat.domain.vo.request.StatusConvertReq;
import com.senjay.archat.common.chat.service.RoomService;
import com.senjay.archat.common.user.domain.vo.response.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminRoomController")
@RequestMapping("/admin/room")
@RequiredArgsConstructor
// 新增room
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/addPrivate")
    public Result createPrivateRoom(@Valid @RequestBody AdminCreatePrivateRoomReq createPrivateRoomReq) {
        roomService.createPrivateRoom(createPrivateRoomReq);
        return Result.success("创建房间成功,开始聊天吧");

    }
   @PostMapping("/convertStatus")
   public Result convertStatus(@Valid @RequestBody StatusConvertReq statusConvertReq) {
        roomService.convertStatus(statusConvertReq);
        return Result.success();
   }

    @DeleteMapping("/deletePrivate/{id}")
    public Result deletePrivateRoom(@NotNull @PathVariable Integer id) {
        roomService.deletePrivateRoom(id);
        return Result.success();
    }
    //  ids: roomId
    @PostMapping("/delBatchOnPrivate")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        roomService.delBatchOnPrivate(ids);
        return Result.success();

    }

    @GetMapping("/listPrivate")
    public Result<Page<RoomFriend>> listRoomFriend(@Valid  RoomSearchDTO roomSearchDTO) {

        return Result.success(roomService.listRoom(roomSearchDTO));
    }
}

