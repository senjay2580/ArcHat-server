package com.senjay.archat.common.chat.domain.vo.response;

import lombok.Data;

@Data
public class UnreadMsgCntResp {
    private Long contactId;
    private Integer count;
}

