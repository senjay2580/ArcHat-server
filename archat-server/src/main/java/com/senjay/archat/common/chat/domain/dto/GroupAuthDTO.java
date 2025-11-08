package com.senjay.archat.common.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class GroupAuthDTO {
    private Long uid;
    private Long roomId;
    private Integer role;
}
