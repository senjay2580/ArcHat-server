package com.senjay.archat.common.user.domain.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModifyReq {


    private String username;
    @NotBlank
    @Size(min = 5, max = 20)
    private String oldPassword;
    @NotBlank
    @Size(min = 5, max = 20)
    private String newPassword;
    @NotBlank
    @Size(min = 5, max = 20)
    private String confirmPassword;
}
