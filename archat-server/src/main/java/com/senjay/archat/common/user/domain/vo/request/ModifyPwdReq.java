package com.senjay.archat.common.user.domain.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ModifyPwdReq {
    @NotBlank
    String oldPassword;
    @NotBlank
    @Size(min = 6, max = 20)
    String newPassword;
    @NotBlank
    @Size(min = 6, max = 20)
    String rePassword;
}
