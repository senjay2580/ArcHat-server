package com.senjay.archat.common.service;

import com.senjay.archat.common.chat.domain.dto.WSChannelExtraDTO;
import com.senjay.archat.common.chat.domain.vo.response.ChatMemberStatisticResp;
import com.senjay.archat.common.chat.service.WebSocketService;
import com.senjay.archat.common.chat.service.impl.WebSocketServiceImpl;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatMemberHelper {

    public ChatMemberStatisticResp getMemberStatistic() {
        ChatMemberStatisticResp resp = new ChatMemberStatisticResp();
        ConcurrentHashMap<Channel, WSChannelExtraDTO> onlineMap = WebSocketServiceImpl.getOnlineMap();
        resp.setOnlineNum((long) onlineMap.size());
        return resp;
    }

}
