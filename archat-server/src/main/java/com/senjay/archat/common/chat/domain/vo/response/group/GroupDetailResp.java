package com.senjay.archat.common.chat.domain.vo.response.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupDetailResp {
    private Long roomId;
    private String name;
    private String avatar;
    private String groupDesc;
    private LocalDateTime createTime;

}
