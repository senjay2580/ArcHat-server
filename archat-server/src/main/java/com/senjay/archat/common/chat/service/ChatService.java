package com.senjay.archat.common.chat.service;

import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.vo.request.ChatMessageBaseReq;
import com.senjay.archat.common.chat.domain.vo.request.ChatMessageReq;
import com.senjay.archat.common.chat.domain.vo.request.WSBaseReq;
import com.senjay.archat.common.chat.domain.vo.response.group.ChatMessageResp;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public interface ChatService {

    void handleChat(TextWebSocketFrame msg, ChannelHandlerContext ctx);

    Long sendMsg(ChatMessageReq request, Long id);
    ChatMessageResp getMsgResp(Long msgId);
    ChatMessageResp getMsgResp(Message message);

    void recallMsg(Long id, ChatMessageBaseReq request);
}
