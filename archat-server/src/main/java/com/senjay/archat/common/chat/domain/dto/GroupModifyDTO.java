package com.senjay.archat.common.chat.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupModifyDTO {
    @NotNull
    private Long roomId;

    private String name;
    private String avatar;
    private String groupDesc;




}
