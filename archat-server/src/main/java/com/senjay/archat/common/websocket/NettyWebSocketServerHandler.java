package com.senjay.archat.common.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.senjay.archat.common.chat.domain.enums.WSReqTypeEnum;
import com.senjay.archat.common.chat.domain.vo.request.WSAuthorize;
import com.senjay.archat.common.chat.domain.vo.request.WSBaseReq;
import com.senjay.archat.common.chat.service.ChatService;
import com.senjay.archat.common.chat.service.WebSocketService;
import com.senjay.archat.common.websocket.webRTC.WebRTCSignalHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * Netty WebSocket 服务器处理器
 * 处理 WebSocket 连接的消息路由和业务逻辑
 */
@Slf4j
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;
    private ChatService chatService;
    private WebRTCSignalHandler webRTCSignalHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseReq.getType());
        
        switch (wsReqTypeEnum) {
            case LOGIN:
                webSocketService.handleLoginReq(ctx.channel());
                log.info("处理登录请求: {}", msg.text());
                break;
            case HEARTBEAT:
                handleHeartbeat(ctx);
                break;
            case CHAT:
                chatService.handleChat(msg, ctx);
                break;
            case WEBRTC_SIGNAL:
                webRTCSignalHandler.handleWebRtcSignal(msg);
                break;
            default:
                log.warn("未知消息类型: {}", wsReqTypeEnum);
        }
    }

    /**
     * 处理心跳消息
     */
    private void handleHeartbeat(ChannelHandlerContext ctx) {
        ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"type\":2}"));
        log.debug("接收到心跳: {}", LocalDateTime.now());
    }

    /**
     * 客户端连接建立时初始化服务
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.webSocketService = SpringUtil.getBean(WebSocketService.class);
        this.chatService = SpringUtil.getBean(ChatService.class);
        this.webRTCSignalHandler = SpringUtil.getBean(WebRTCSignalHandler.class);
    }

    /**
     * 客户端连接移除时处理
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        handleUserOffline(ctx);
    }

    /**
     * 连接变为非活跃状态时处理
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("连接变为非活跃状态: {}", ctx.channel().id());
        handleUserOffline(ctx);
    }

    /**
     * 处理用户离线
     */
    private void handleUserOffline(ChannelHandlerContext ctx) {
        this.webSocketService.removed(ctx.channel());
        ctx.channel().close();
    }

    /**
     * 处理用户事件：心跳检测和握手完成
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.warn("连接读空闲超时，关闭连接: {}", ctx.channel().id());
                handleUserOffline(ctx);
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            handleHandshakeComplete(ctx);
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 处理握手完成事件
     */
    private void handleHandshakeComplete(ChannelHandlerContext ctx) {
        this.webSocketService.connect(ctx.channel());
        String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
        if (StrUtil.isNotBlank(token)) {
            this.webSocketService.authorize(ctx.channel(), new WSAuthorize(token));
        }
    }

    /**
     * 处理异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("WebSocket连接异常: {}", cause.getMessage(), cause);
        ctx.channel().close();
    }


}
