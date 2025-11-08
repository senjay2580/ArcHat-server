package com.senjay.archat.common.chat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.senjay.archat.common.chat.domain.vo.request.GroupMsgQueryReq;
import com.senjay.archat.common.chat.domain.vo.request.MessageQueryReq;
import com.senjay.archat.common.chat.domain.vo.response.MessageResp;
import com.senjay.archat.common.chat.domain.vo.response.group.ChatMessageResp;

import java.util.List;

public interface MessageService {
    void sendTextToUid(Long fromUid,Long roomId, String content);

    List<MessageResp> listMessage(MessageQueryReq messageQueryReq);

    MessageResp getMessage(Long id);

    IPage<ChatMessageResp> listGroupMessage(GroupMsgQueryReq groupMsgQueryReq);
}
