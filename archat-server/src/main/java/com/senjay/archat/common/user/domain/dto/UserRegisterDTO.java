package com.senjay.archat.common.user.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDTO {
    @NotBlank
    @Size(min = 5, max = 20)
    private String  username;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

//    确认密码
    @NotBlank
    @Size(min = 6, max = 20)
    private String confirmPassword;
}
