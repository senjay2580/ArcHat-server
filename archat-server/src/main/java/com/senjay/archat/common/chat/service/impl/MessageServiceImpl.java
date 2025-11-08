package com.senjay.archat.common.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.chat.dao.ContactDao;
import com.senjay.archat.common.chat.dao.MessageDao;
import com.senjay.archat.common.chat.dao.RoomDao;
import com.senjay.archat.common.chat.dao.RoomFriendDao;
import com.senjay.archat.common.chat.dao.RoomGroupDao;
import com.senjay.archat.common.chat.domain.entity.Contact;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.entity.Room;
import com.senjay.archat.common.chat.domain.entity.RoomFriend;
import com.senjay.archat.common.chat.domain.entity.RoomGroup;
import com.senjay.archat.common.chat.domain.enums.MessageStatusEnum;
import com.senjay.archat.common.chat.domain.enums.MessageTypeEnum;
import com.senjay.archat.common.chat.domain.enums.RoomStatusEnum;
import com.senjay.archat.common.chat.domain.enums.RoomTypeEnum;
import com.senjay.archat.common.chat.domain.vo.request.BasePageReq;
import com.senjay.archat.common.chat.domain.vo.request.GroupMsgQueryReq;
import com.senjay.archat.common.chat.domain.vo.request.MessageQueryReq;
import com.senjay.archat.common.chat.domain.vo.request.UserAndRoomReq;
import com.senjay.archat.common.chat.domain.vo.response.MessageResp;
import com.senjay.archat.common.chat.domain.vo.response.group.ChatMessageResp;
import com.senjay.archat.common.chat.mapper.MessageMapper;
import com.senjay.archat.common.chat.service.MessageService;
import com.senjay.archat.common.constant.MQConstant;
import com.senjay.archat.common.exception.RoomException;
import com.senjay.archat.common.exception.errorEnums.BusinessErrorEnum;
import com.senjay.archat.common.exception.errorEnums.RoomErrorEnum;
import com.senjay.archat.common.service.MQService.MQProducer;
import com.senjay.archat.common.service.Strategy.msg.AbstractMsgHandler;
import com.senjay.archat.common.service.Strategy.msg.MsgHandlerFactory;
import com.senjay.archat.common.service.adapter.MessageAdapter;
import com.senjay.archat.common.exception.BusinessException;
import com.senjay.archat.common.util.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageMapper messageMapper;
    private final MessageDao messageDao;
    private final ContactDao contactDao;
    private final MQProducer mqProducer;
    private final RoomDao roomDao;
    private final RoomFriendDao roomFriendDao;
    private final RoomGroupDao roomGroupDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendTextToUid(Long fromUid, Long roomId, String content) {
        Message message = Message.builder()
                .fromUid(fromUid)
                .roomId(roomId)
                .content(content)
                .status(MessageStatusEnum.NORMAL.getStatus())
                .type(MessageTypeEnum.TEXT.getType())
                .createTime(LocalDateTime.now())
                .build();

        messageDao.save(message);
        
        // 更新会话最新消息信息
        contactDao.lambdaUpdate()
                .set(Contact::getActiveTime, message.getCreateTime())
                .set(Contact::getLastMsgId, message.getId())
                .eq(Contact::getRoomId, roomId)
                .update();
    }


    @Override
    public List<MessageResp> listMessage(MessageQueryReq messageQueryReq) {
        Long roomId = messageQueryReq.getRoomId();
        Long userId = UserHolder.get().getId();
        
        checkRoomLegal(roomId);
        
        // 异步更新用户阅读时间
        mqProducer.sendMsg(MQConstant.UPDATE_EXCHANGE, MQConstant.CONTACT_BINDING_KEY, 
                          new UserAndRoomReq(userId, roomId));
        
        return messageMapper.listMessage(messageQueryReq);
    }

    @Override
    public MessageResp getMessage(Long id) {
        Message message = messageDao.lambdaQuery().eq(Message::getId, id).one();
        MessageResp messageResp = BeanUtil.copyProperties(message, MessageResp.class);
        
        AbstractMsgHandler msgHandler = MsgHandlerFactory.getStrategyNoNull(message.getType());
        messageResp.setContent(msgHandler.showContactMsg(message));
        return messageResp;
    }

    @Override
    public IPage<ChatMessageResp> listGroupMessage(GroupMsgQueryReq groupMsgQueryReq) {
        Long roomId = groupMsgQueryReq.getRoomId();
        checkRoomLegal(roomId);
        
        BasePageReq basePageReq = groupMsgQueryReq.getBasePageReq();
        Page<Message> page = new Page<>(basePageReq.getPage(), basePageReq.getPageSize());
        
        // 异步更新用户阅读时间
        mqProducer.sendMsg(MQConstant.UPDATE_EXCHANGE, MQConstant.CONTACT_BINDING_KEY, 
                          new UserAndRoomReq(UserHolder.get().getId(), roomId));
        
        // 分页查询消息
        Page<Message> messagePage = messageDao.lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .eq(Message::getStatus, MessageStatusEnum.NORMAL.getStatus())
                .orderByDesc(Message::getCreateTime)
                .page(page);
                
        List<ChatMessageResp> chatMessageResp = MessageAdapter.buildMsgResp(messagePage.getRecords());
        Page<ChatMessageResp> chatPage = new Page<>(page.getCurrent(), page.getSize(), messagePage.getTotal());
        chatPage.setRecords(chatMessageResp);
        return chatPage;
    }

    /**
     * 校验房间是否合法（正常可用）
     *
     * @param roomId 房间id
     */
    private void checkRoomLegal(Long roomId) {
        Room room = roomDao.getById(roomId);
        if (room == null) {
            throw new RoomException(RoomErrorEnum.ROOM_NOT_FOUND);
        }

        RoomTypeEnum roomTypeEnum = RoomTypeEnum.of(room.getType());

        switch (roomTypeEnum) {
            case GROUP:
                RoomGroup roomGroup = roomGroupDao.lambdaQuery().eq(RoomGroup::getRoomId, roomId).one();
                if (Objects.equals(roomGroup.getDeleteStatus(), RoomStatusEnum.FORBIDDEN.getCode())) {
                    throw new RoomException(RoomErrorEnum.ROOM_CLOSED);
                }
                break;
            case FRIEND:
                RoomFriend roomFriend = roomFriendDao.lambdaQuery().eq(RoomFriend::getRoomId, roomId).one();
                if (Objects.equals(roomFriend.getStatus(), RoomStatusEnum.FORBIDDEN.getCode())) {
                    throw new RoomException(RoomErrorEnum.ROOM_CLOSED);
                }
                break;
            default:
                throw new BusinessException(BusinessErrorEnum.SYSTEM_ERROR);
        }

    }

}

