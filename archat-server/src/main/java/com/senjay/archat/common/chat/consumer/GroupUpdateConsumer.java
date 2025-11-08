package com.senjay.archat.common.chat.consumer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.senjay.archat.common.chat.dao.ContactDao;
import com.senjay.archat.common.chat.dao.GroupMemberDao;
import com.senjay.archat.common.chat.dao.MessageDao;
import com.senjay.archat.common.chat.domain.dto.RoomMessageDTO;
import com.senjay.archat.common.chat.domain.entity.Contact;
import com.senjay.archat.common.chat.domain.entity.GroupMember;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.vo.request.UserAndRoomReq;
import com.senjay.archat.common.constant.MQConstant;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class GroupUpdateConsumer {

    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private ContactDao contactDao;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstant.MEMBER_REMOVED_QUEUE),
            exchange = @Exchange(name = MQConstant.UPDATE_EXCHANGE,type = ExchangeTypes.DIRECT),
            key = {MQConstant.GROUP_CLEAN_UP_BINDING_KEY}

    ))
    public void listenRemovedGroup(RoomMessageDTO dto) {
        Long roomId = dto.getRoomId();
        List<Long> uids = dto.getUids();
        removeGroupMember(uids, roomId);

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstant.MEMBER_REMOVED_QUEUE),
            exchange = @Exchange(name = MQConstant.UPDATE_EXCHANGE,type = ExchangeTypes.DIRECT),
            key = {MQConstant.MEMBER_CLEAN_UP_BINDING_KEY}

    ))
    public void listenRemovedMember(RoomMessageDTO dto) {
        Long roomId = dto.getRoomId();
        List<Long> uids = dto.getUids();
        removeGroupMember(uids, roomId);

    }


    //      groupmember  contact  删除 message 不删除
    @Transactional(rollbackFor = Exception.class)
    public void removeGroupMember(List<Long> uids, Long roomId) {
        groupMemberDao.remove(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, roomId)
                        .in(!uids.isEmpty(),GroupMember::getUid, uids)
        );
        contactDao.remove(new LambdaQueryWrapper<Contact>()
                .eq(Contact::getRoomId, roomId)
                .in(!uids.isEmpty(),Contact::getUid, uids)
        );
    }
}
