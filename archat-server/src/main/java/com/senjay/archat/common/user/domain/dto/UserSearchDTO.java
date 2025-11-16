package com.senjay.archat.common.user.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSearchDTO {
    private String keyword;
    private Integer expMin;
    private Integer expMax;
    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;

}
