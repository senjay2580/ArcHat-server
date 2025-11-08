package com.senjay.archat.common.event.listener;

import com.senjay.archat.common.chat.domain.dto.MsgSendMessageDTO;
import com.senjay.archat.common.constant.MQConstant;
import com.senjay.archat.common.event.MessageSendEvent;
import com.senjay.archat.common.service.MQService.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
/**
 * 消息发送监听器
 */
@Slf4j
@Component
public class MessageSendListener {
    @Autowired
    private MQProducer mqProducer;

// 在事务内发布了一个事件，只有当事务真正提交成功了”，再去推送 到监听器
    @TransactionalEventListener(classes = MessageSendEvent.class, fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Long msgId = event.getMsgId();
//        根据 消费的时候 查到的message.roomId 业务逻辑判断 群发还是/私聊发送
        mqProducer.sendMsg(MQConstant.MSG_EXCHANGE, MQConstant.CHAT_BINDING_KEY, new MsgSendMessageDTO(msgId));
    }


}
