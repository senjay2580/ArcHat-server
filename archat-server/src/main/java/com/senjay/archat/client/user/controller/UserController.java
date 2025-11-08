package com.senjay.archat.client.user.controller;

import com.senjay.archat.common.user.domain.dto.ModifyAvatarDTO;
import com.senjay.archat.common.user.domain.dto.UserLoginDTO;
import com.senjay.archat.common.user.domain.dto.UserRegisterDTO;
import com.senjay.archat.common.user.domain.vo.request.ModifyNameReq;
import com.senjay.archat.common.user.domain.vo.request.ModifyPwdReq;
import com.senjay.archat.common.user.domain.vo.request.TokenReq;
import com.senjay.archat.common.user.domain.vo.response.Result;
import com.senjay.archat.common.user.domain.vo.response.UserProfileResp;
import com.senjay.archat.common.user.service.UserService;
import com.senjay.archat.common.util.AliOssUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.UUID;

@RestController("clientUserController")
@RequestMapping("/client/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AliOssUtil aliOssUtil;

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

    @Operation(summary = "修改用户名")
    @PostMapping("/modifyName")
    public Result<String> modifyName(@Valid @RequestBody ModifyNameReq modifyNameReq) {
        return Result.success(userService.modifyName(modifyNameReq));
    }
    @Operation(summary = "修改密码")
    @PostMapping("/modifyPwd")
    public Result<String> modifyPassword(@Valid @RequestBody ModifyPwdReq modifyPwdReq, 
                                       @RequestHeader("Authorization") String token) {
        return Result.success(userService.modifyPwd(modifyPwdReq, token));
    }

    @Operation(summary = "上传头像")
    @PostMapping("/upload")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            return Result.fail("文件格式不正确");
        }
        
        String fileName = UUID.randomUUID().toString() + 
                         originalFilename.substring(originalFilename.lastIndexOf("."));
        String url = aliOssUtil.upload(file.getBytes(), fileName);
        
        return Result.success(url);
    }

    @Operation(summary = "修改头像")
    @PostMapping("/modify/avatar")
    public Result<Void> modifyAvatar(@Valid @RequestBody ModifyAvatarDTO modifyAvatarDTO) {
        userService.modifyAvatar(modifyAvatarDTO);
        return Result.success();
    }

}
