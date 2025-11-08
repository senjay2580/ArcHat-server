package com.senjay.archat.common.chat.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSChatReq {

    private Integer type;
    private Long targetUid;
    private Long roomId;
    private String content;


}
