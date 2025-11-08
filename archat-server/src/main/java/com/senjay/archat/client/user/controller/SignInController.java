package com.senjay.archat.client.user.controller;

import com.senjay.archat.common.service.SignInHelperService;
import com.senjay.archat.common.user.domain.vo.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 签到接口
@RestController
@RequiredArgsConstructor
@RequestMapping("/client/signIn")
public class SignInController {
    private final SignInHelperService signInHelperService;

    @Operation(summary = "签到")
    @PostMapping
    public Result signIn() {
        signInHelperService.signIn();
        return Result.success();
    }
    @Operation(summary = "查询当月的签到情况")
    @GetMapping("/detail")
    public Result<Long> getSignInDetailOfMonth() {
        return Result.success(signInHelperService.getSignInDetailOfMonth());
    }
}
