package com.senjay.archat.common.chat.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberStatisticResp {

    @Schema(description = "在线人数")
    private Long onlineNum;

    @Schema(description = "总人数")
    @Deprecated
    private Long totalNum;
}
