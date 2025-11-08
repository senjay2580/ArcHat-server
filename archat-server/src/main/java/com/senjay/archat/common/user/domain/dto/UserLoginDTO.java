package com.senjay.archat.common.user.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



// DTO
// DTO是接收前端数据的 所以一般就是在这里进行校验逻辑
// 用户基本信息
// controller层接收req/DTO 服务层进行封装信息为entity 到DAO层,然后DAO 层就利用mybatis plus操作 与数据库对于的实体类 --entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 还有自定义校验 以及分组校验功能 @NotNull @NotEmpty @Email
//@Pattern(regexp = "正则表达式", message = "错误提示")

public class UserLoginDTO {
    @NotBlank
    @Size(min = 5, max = 20)
    private String  username;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;
}
