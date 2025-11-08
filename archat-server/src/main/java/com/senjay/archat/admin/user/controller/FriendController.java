package com.senjay.archat.admin.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.user.domain.dto.FriendDTO;
import com.senjay.archat.common.user.domain.dto.FriendSearchDTO;
import com.senjay.archat.common.user.domain.entity.Friend;
import com.senjay.archat.common.user.service.FriendService;
import com.senjay.archat.common.user.domain.vo.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController("adminFriendController")
@RequestMapping("/admin/friend")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;


    @Operation(summary = "新建好友关系")
    @PostMapping("/add")
    public Result<String> addFriend(@Valid @RequestBody FriendDTO friendDTO) {

        String isSuccess = friendService.createAndAdd(friendDTO);
        return Result.success(isSuccess);
    }

    @Operation(summary = "删除申请记录/好友关系")
    @DeleteMapping("/{id}")
    public Result<String> deleteFriend(@PathVariable Long id) {

        return Result.success(friendService.deleteById(id));
    }
    @Operation(summary = "查询关系列表")
    @GetMapping("/list")
    public Result<Page<Friend>> listFriend(@Valid FriendSearchDTO friendSearchDTO) {
        return Result.success(friendService.listFriend(friendSearchDTO));

    }



}
