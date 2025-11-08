package com.senjay.archat.common.service.Strategy.msg;


import com.senjay.archat.common.chat.dao.MessageDao;
import com.senjay.archat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.enums.MessageTypeEnum;
import com.senjay.archat.common.chat.domain.vo.request.msg.MessageExtra;
import com.senjay.archat.common.chat.domain.vo.request.msg.MsgRecall;
import com.senjay.archat.common.event.MessageRecallEvent;
import com.senjay.archat.common.user.dao.UserDao;
import com.senjay.archat.common.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * Description: 撤回文本消息
 *
 */
@Component
// 撤回的消息类型有好多种 使用object
public class RecallMsgHandler extends AbstractMsgHandler<Object> {
    @Autowired
    private MessageDao messageDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private UserDao userDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.RECALL;
    }



    //    在父类/抽象类中提供一个“默认实现”，子类如果不重写，调用就会报错，提醒开发者必须重写这个方法
//    或者某个子类不支持这项操作，但为了满足接口/抽象类结构，只能把方法写上，但调用时抛异常
    @Override
    public void saveMsg(Message msg, Object body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object showMsg(Message msg) {
        MsgRecall recall = msg.getExtra().getRecall();
//        User userInfo = userCache.getUserInfo(recall.getRecallUid());
        User userInfo = userDao.getById(recall.getRecallUid());
        if (!Objects.equals(recall.getRecallUid(), msg.getFromUid())) {
            return "管理员\"" + userInfo.getUsername() + "\"撤回了一条成员消息";
        }
        return "\"" + userInfo.getUsername() + "\"撤回了一条消息";
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "原消息已被撤回";
    }

    public void recall(Long recallUid, Message message) {//todo 消息覆盖问题用版本号解决
        MessageExtra extra = message.getExtra();
//        扩展json 中打上撤回信息
        extra.setRecall(new MsgRecall(recallUid, new Date()));
        Message update = new Message();
        update.setId(message.getId());
        update.setType(MessageTypeEnum.RECALL.getType());
        update.setExtra(extra);
        messageDao.updateById(update);
//        序列化传输消息撤回消息
        applicationEventPublisher.publishEvent(new MessageRecallEvent(this, new ChatMsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));

    }

    @Override
    public String showContactMsg(Message msg) {
        return "撤回了一条消息";
    }
}
