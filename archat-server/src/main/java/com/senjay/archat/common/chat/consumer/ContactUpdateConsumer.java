package com.senjay.archat.common.chat.consumer;

import com.senjay.archat.common.chat.dao.ContactDao;
import com.senjay.archat.common.chat.domain.dto.MsgSendMessageDTO;
import com.senjay.archat.common.chat.domain.vo.request.MessageQueryReq;
import com.senjay.archat.common.chat.domain.vo.request.UserAndRoomReq;
import com.senjay.archat.common.constant.MQConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContactUpdateConsumer {
    @Autowired
    private ContactDao contactDao;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstant.CONTACT_QUEUE),
            exchange = @Exchange(name = MQConstant.UPDATE_EXCHANGE,type = ExchangeTypes.DIRECT),
            key = {MQConstant.CONTACT_BINDING_KEY}

    ))
    public void listenUpdateContact(UserAndRoomReq req) {
        Long roomId = req.getRoomId();
        Long uid = req.getUid();
        contactDao.updateReadTime(roomId,uid);
    }

}
