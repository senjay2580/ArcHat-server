package com.senjay.archat.client.user.controller;

import com.senjay.archat.common.user.domain.dto.DateKeyDTO;
import com.senjay.archat.common.user.domain.vo.response.Result;
import com.senjay.archat.common.user.service.UserVisitService;
import com.senjay.archat.common.util.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/client/visit")
@RequiredArgsConstructor
public class UserVisitController {
    private final UserVisitService userVisitService;

    @Operation(summary = "增加每日访问量")
    @PostMapping("/add")
    public Result addVisitor() {
        String uid = UserHolder.get().getId().toString();
        userVisitService.addVisitor(uid);
        return Result.success();
    }

    @Operation(summary = "查看访问量")
    @PostMapping("/count")
    public Result<String> countVisitor(@Valid @RequestBody DateKeyDTO dateKeyDTO) {
        return Result.success(userVisitService.countVisitor(dateKeyDTO.getDateKey()));
    }

}
