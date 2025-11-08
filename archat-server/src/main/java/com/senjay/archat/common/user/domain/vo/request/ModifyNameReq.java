package com.senjay.archat.common.user.domain.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class ModifyNameReq {
    @NotBlank
    @Size(min = 5, max = 20)
    private String username;
}
