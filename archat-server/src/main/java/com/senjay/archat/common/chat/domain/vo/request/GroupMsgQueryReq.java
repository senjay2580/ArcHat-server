package com.senjay.archat.common.chat.domain.vo.request;

import lombok.Data;

@Data
public class GroupMsgQueryReq {
    private Long roomId;
    private BasePageReq basePageReq;

}
