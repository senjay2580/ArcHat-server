package com.senjay.archat.common.chat.service;

import com.senjay.archat.common.chat.domain.vo.request.WSAuthorize;
import com.senjay.archat.common.chat.domain.vo.response.ws.WSBaseResp;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public interface WebSocketService {

    void handleLoginReq(Channel channel);

    void removed(Channel channel);

    void connect(Channel channel);
    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     * @param skipUid    需要跳过的人
     */
    void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid);

    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     */
    void sendToAllOnline(WSBaseResp<?> wsBaseResp);
    void authorize(Channel channel, WSAuthorize wsAuthorize);
    void sendToUid(WSBaseResp<?> wsBaseResp, Long uid);

}
