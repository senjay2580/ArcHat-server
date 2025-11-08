package com.senjay.archat.common.user.domain.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DateKeyDTO {
    @Pattern(regexp = "^\\d{8}$", message = "日期格式错误，应为yyyyMMdd格式")
    private String dateKey;
}
