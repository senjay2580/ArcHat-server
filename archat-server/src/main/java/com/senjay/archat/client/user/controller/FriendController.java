package com.senjay.archat.client.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.senjay.archat.common.user.domain.dto.FriendApplyQueryDTO;
import com.senjay.archat.common.user.domain.vo.request.FriendApplyDealWithReq;
import com.senjay.archat.common.user.domain.vo.request.FriendApplyReq;
import com.senjay.archat.common.user.domain.vo.request.FriendSearchReq;
import com.senjay.archat.common.user.domain.vo.response.FriendApplyResp;
import com.senjay.archat.common.user.domain.vo.response.FriendSearchResp;
import com.senjay.archat.common.user.service.FriendService;
import com.senjay.archat.common.user.domain.vo.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 好友管理控制器
 * <p>
 * 提供好友相关的操作功能，包括搜索好友、发送好友申请、处理好友申请等。
 * 支持好友列表管理和申请记录查询。
 * </p>
 *
 * @author senjay
 * @since 1.0.0
 */
@RestController("clientFriendController")
@RequestMapping("/client/friend")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    /**
     * 搜索好友
     *
     * @param friendSearchReq 好友搜索请求
     * @return 搜索结果列表
     */
    @Operation(summary = "搜索好友")
    @PostMapping("/search")
    public Result<IPage<FriendSearchResp>> search(@Valid @RequestBody FriendSearchReq friendSearchReq) {
        return Result.success(friendService.search(friendSearchReq));
    }

    /**
     * 发送好友申请
     *
     * @param friendApplyReq 好友申请请求
     * @return 操作结果
     */
    @Operation(summary = "发送好友申请")
    @PostMapping("/add")
    public Result<String> addFriend(@Valid @RequestBody FriendApplyReq friendApplyReq) {
        return Result.success(friendService.addFriend(friendApplyReq));
    }
    /**
     * 查询是否为好友关系
     *
     * @param fid 好友用户ID
     * @return 是否为好友
     */
    @Operation(summary = "查询是否是自己的好友")
    @GetMapping("/isFriend")
    public Result<Boolean> isFriend(Long fid) {
        return Result.success(friendService.checkFriendship(fid));
    }

    /**
     * 处理好友申请
     *
     * @param friendApplyDealWithReq 好友申请处理请求
     * @return 处理结果
     */
    @Operation(summary = "处理好友申请")
    @PostMapping("/dealWith")
    public Result<String> dealWithFriend(@Valid @RequestBody FriendApplyDealWithReq friendApplyDealWithReq) {
        return Result.success(friendService.approveOrNot(friendApplyDealWithReq));
    }

    /**
     * 删除好友关系
     *
     * @param friendId 好友用户ID
     * @return 删除结果
     */
    @Operation(summary = "删除申请记录/好友关系")
    @DeleteMapping("/delete/{friendId}")
    public Result<String> deleteFriend(@PathVariable Long friendId) {
        return Result.success(friendService.deleteFriend(friendId));
    }
    /**
     * 获取当前用户的好友列表
     *
     * @param uid 用户ID
     * @return 好友列表
     */
    @Operation(summary = "获取当前好友列表")
    @GetMapping("/listFriend")
    public Result<List<FriendSearchResp>> listFriend(Long uid) {
        return Result.success(friendService.listFriends(uid));
    }
    /**
     * 获取当前用户发送的申请列表
     *
     * @param friendApplyQueryDTO 好友申请查询条件
     * @return 申请列表
     */
    @Operation(summary = "获取当前我申请列表")
    @PostMapping("/list/myApply")
    public Result<IPage<FriendApplyResp>> listMyApply(@Valid @RequestBody FriendApplyQueryDTO friendApplyQueryDTO) {
        return Result.success(friendService.listMyApply(friendApplyQueryDTO));
    }

    /**
     * 获取当前用户收到的申请列表
     *
     * @param friendApplyQueryDTO 好友申请查询条件
     * @return 申请列表
     */
    @Operation(summary = "获取当前收到的申请列表")
    @PostMapping("/list/myReceive")
    public Result<IPage<FriendApplyResp>> listMyReceive(@Valid @RequestBody FriendApplyQueryDTO friendApplyQueryDTO) {
        return Result.success(friendService.listMyReceive(friendApplyQueryDTO));
    }
}
