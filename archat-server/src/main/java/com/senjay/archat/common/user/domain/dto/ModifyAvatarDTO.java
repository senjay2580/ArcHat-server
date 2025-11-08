package com.senjay.archat.common.user.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;



@Data
public class ModifyAvatarDTO {
    @NotBlank(message = "头像地址不能为空")
    @Pattern(regexp = "https?://.*\\.(jpg|jpeg|png)", message = "头像地址格式非法")
    private String avatar;
}
