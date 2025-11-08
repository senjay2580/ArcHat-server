package com.senjay.archat.common.user.consumer;

import com.senjay.archat.common.chat.domain.dto.PushMessageDTO;
import com.senjay.archat.common.chat.domain.enums.WSPushTypeEnum;
import com.senjay.archat.common.chat.service.WebSocketService;
import com.senjay.archat.common.constant.MQConstant;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


// 消息队列消费者
@Component
public class PushConsumer {

    @Autowired
    private WebSocketService webSocketService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstant.PUBLIC_PUSH_QUEUE),
            exchange = @Exchange(name = MQConstant.PUSH_EXCHANGE,type = ExchangeTypes.DIRECT),
            key = {MQConstant.ONLINE_BINDING_KEY}

    ))
    public void listenOnlinePush(PushMessageDTO message) {
        WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(message.getPushType());
        if (wsPushTypeEnum == null) {
            return;
        }

        switch (wsPushTypeEnum) {
            case USER:
                message.getUidList().forEach(uid -> {
                    webSocketService.sendToUid(message.getWsBaseMsg(), uid);
                });
//                一个一个发过去
                break;
            case ALL:
                webSocketService.sendToAllOnline(message.getWsBaseMsg(), null);
                break;
        }
    }
}
