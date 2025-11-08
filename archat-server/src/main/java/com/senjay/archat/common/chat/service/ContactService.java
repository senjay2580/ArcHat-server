package com.senjay.archat.common.chat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.senjay.archat.common.chat.domain.vo.request.BasePageReq;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupQueryResp;
import com.senjay.archat.common.chat.domain.vo.response.PrivateContactResp;
import com.senjay.archat.common.chat.domain.vo.response.UnreadMsgCntResp;

public interface ContactService {
    IPage<PrivateContactResp> listPrivate(BasePageReq contactQueryReq);

    IPage<GroupQueryResp> listGroup(BasePageReq contactQueryReq);

    IPage<UnreadMsgCntResp> getUnreadMsgCnt(BasePageReq basePageReq);
}
