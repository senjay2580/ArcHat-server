package com.senjay.archat.common.chat.consumer;


import com.senjay.archat.common.chat.dao.ContactDao;
import com.senjay.archat.common.chat.dao.GroupMemberDao;
import com.senjay.archat.common.chat.dao.MessageDao;
import com.senjay.archat.common.chat.dao.RoomDao;
import com.senjay.archat.common.chat.domain.dto.MsgSendMessageDTO;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.entity.Room;
import com.senjay.archat.common.chat.domain.enums.RoomTypeEnum;
import com.senjay.archat.common.chat.domain.vo.response.group.ChatMessageResp;
import com.senjay.archat.common.chat.service.ChatService;
import com.senjay.archat.common.chat.service.WebSocketService;
import com.senjay.archat.common.constant.MQConstant;
import com.senjay.archat.common.service.PushService;

import com.senjay.archat.common.service.adapter.WSAdapter;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// 消息队列消费者
@Component
public class MsgSendConsumer {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private ContactDao contactDao;
    @Autowired
    private PushService pushService;
    @Autowired
    private ChatService chatService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstant.CHAT_MSG_QUEUE),
            exchange = @Exchange(name = MQConstant.MSG_EXCHANGE,type = ExchangeTypes.DIRECT),
            key = {MQConstant.CHAT_BINDING_KEY}

    ))
    public void listenSendMsg(MsgSendMessageDTO messageDTO) {
//        获取房间性质 群聊 or 单聊 决定如何发布
        Message message = messageDao.getById(messageDTO.getMsgId());
        Room room = roomDao.getById(message.getRoomId());
        RoomTypeEnum roomTypeEnum = RoomTypeEnum.of(room.getType());

//       获取ws消息中的消息体
        ChatMessageResp msgResp = chatService.getMsgResp(message);
//        接收对象 list 统一处理 1 和 多
        List<Long> memberUidList = new ArrayList<>();
        switch (roomTypeEnum) {
            case FRIEND:
                break;
            case GROUP:
                memberUidList = groupMemberDao.getAllMemberUid(room.getId());
                break;
            default:
                break;
        }
        //更新所有群成员的会话时间
        if(!memberUidList.isEmpty()){
            contactDao.refreshOrCreateActiveTime(room.getId(), memberUidList, message.getId(), message.getCreateTime());
            // 先将消息包装一下 推送给房间内成员
/*相当于发送到另一个消息队列中！！*/
            pushService.sendPushMsg(WSAdapter.buildMsgSend(msgResp), memberUidList);

        }

    }

}
