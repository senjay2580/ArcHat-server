package com.senjay.archat.common.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.rmi.server.UID;

/**
 * Description: netty工具类
    它的作用是在 Netty 的每一个连接通道（Channel）中存储、读取一些关键数据，例如用户 token、用户 uid、客户端 IP、握手信息等。
 */

public class NettyUtil {

    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");
    public static AttributeKey<String> IP = AttributeKey.valueOf("ip");
    public static AttributeKey<Long> UID = AttributeKey.valueOf("uid");
    public static AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");

    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey, T data) {
        Attribute<T> attr = channel.attr(attributeKey);
        attr.set(data);
    }

    public static <T> T getAttr(Channel channel, AttributeKey<T> ip) {
        return channel.attr(ip).get();
    }
}

//NettyUtil 是一个专门用于管理每个连接（Channel）中存储的临时状态数据（如登录 token、IP、
//UID 等）的工具类，在构建长连接服务（如 WebSocket）时非常实用。它让你可以像使用“会话”一样，给每个连接挂载自己的状态信息。