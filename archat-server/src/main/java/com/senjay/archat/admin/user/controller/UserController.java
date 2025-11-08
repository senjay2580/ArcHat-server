package com.senjay.archat.admin.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.user.domain.dto.UserEditFormDTO;
import com.senjay.archat.common.user.domain.dto.UserLoginDTO;
import com.senjay.archat.common.user.domain.dto.UserRegisterDTO;
import com.senjay.archat.common.user.domain.dto.UserSearchDTO;
import com.senjay.archat.common.user.domain.entity.User;
import com.senjay.archat.common.user.domain.vo.request.TokenReq;
import com.senjay.archat.common.user.domain.vo.response.UserProfileResp;
import com.senjay.archat.common.user.service.UserService;
import com.senjay.archat.common.user.domain.vo.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminUserController")
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Result.success();
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<UserProfileResp> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return Result.success(userService.login(userLoginDTO));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<String> logout(@RequestBody TokenReq tokenReq) {
        return Result.success(userService.logout(tokenReq));
    }
    @Operation(summary = "查询用户列表")
    @GetMapping("/list")
    public Result<Page<User>> listUser(@Valid UserSearchDTO userSearchDTO) {
        return Result.success(userService.listUser(userSearchDTO));
    }

    @Operation(summary = "查询所有用户")
    @GetMapping("/all")
    public Result<List<User>> listAllUser() {
        return Result.success(userService.getAll());
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        return Result.success(userService.deleteById(id));
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteBatch")
    public Result<String> deleteBatch(@Valid @RequestBody List<Long> ids) {
        return Result.success(userService.deleteBatch(ids));
    }

    @Operation(summary = "新增/修改用户")
    @PutMapping
    public Result<String> updateUser(@Valid @RequestBody UserEditFormDTO userEditFormDTO) {
        // 只更新不为 null 的字段（如果配置了自动忽略 null 字段）
        return Result.success(userService.updateById(userEditFormDTO));
    }

}
