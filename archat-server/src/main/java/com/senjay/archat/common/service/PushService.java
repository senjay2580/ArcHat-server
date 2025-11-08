package com.senjay.archat.common.service;

import com.senjay.archat.common.chat.domain.dto.PushMessageDTO;
import com.senjay.archat.common.chat.domain.vo.response.ws.WSBaseResp;
import com.senjay.archat.common.constant.MQConstant;
import com.senjay.archat.common.service.MQService.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 这个服务调用 消息队列生产者 使用消息队列发送 ws类型的响应消息
@Service
public class PushService {
    @Autowired
    private MQProducer mqProducer;

    public void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList) {
        mqProducer.sendMsg(MQConstant.PUSH_EXCHANGE, MQConstant.ONLINE_BINDING_KEY, new PushMessageDTO(uidList, msg));
    }
//    public void sendPushMsg(WSBaseResp<?> msg, Long uid) {
//        mqProducer.sendMsg(MQConstant.PUSH_EXCHANGE, new PushMessageDTO(uid, msg));
//    }

    public void sendPushMsg(WSBaseResp<?> msg) {
        mqProducer.sendMsg(MQConstant.PUSH_EXCHANGE, MQConstant.ONLINE_BINDING_KEY, new PushMessageDTO(msg));
    }

}
