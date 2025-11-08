package com.senjay.archat.common.websocket.webRTC;

import cn.hutool.json.JSONUtil;
import com.senjay.archat.common.chat.domain.enums.WSRespTypeEnum;
import com.senjay.archat.common.chat.domain.vo.request.WSBaseReq;
import com.senjay.archat.common.chat.domain.vo.response.ws.WSBaseResp;
import com.senjay.archat.common.chat.service.WebSocketService;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebRTCSignalHandler {

    private final WebSocketService webSocketService;

    public void handleWebRtcSignal(TextWebSocketFrame message) {
        WSBaseReq wsBaseReq = JSONUtil.toBean(message.text(), WSBaseReq.class);
        WebRTCSignalMessage webRTCSignalMessage = JSONUtil.toBean(wsBaseReq.getData(), WebRTCSignalMessage.class);
        
        if (webRTCSignalMessage == null || webRTCSignalMessage.getTargetUserId() == null) {
            log.error("WebRTC信令消息或目标用户ID为空");
            return;
        }
        
        forwardSignal(webRTCSignalMessage);
    }

    private void forwardSignal(WebRTCSignalMessage message) {
        Long targetUserId = message.getTargetUserId();
        WSBaseResp<WebRTCSignalMessage> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.WEBRTC_SIGNAL_FORWARD.getType());
        wsBaseResp.setData(message);
        webSocketService.sendToUid(wsBaseResp, targetUserId);
    }
}