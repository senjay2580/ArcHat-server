package com.senjay.archat.common.service.Strategy.msg;

import cn.hutool.core.collection.CollectionUtil;

import com.senjay.archat.common.chat.dao.MessageDao;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.enums.MessageStatusEnum;
import com.senjay.archat.common.chat.domain.enums.MessageTypeEnum;
import com.senjay.archat.common.chat.domain.vo.request.msg.MessageExtra;
import com.senjay.archat.common.chat.domain.vo.request.msg.TextMsgReq;
import com.senjay.archat.common.chat.domain.vo.response.group.TextMsgResp;
import com.senjay.archat.common.service.adapter.MessageAdapter;
import com.senjay.archat.common.user.domain.entity.User;
import com.senjay.archat.common.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Description: 普通文本消息
 */
@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgReq> {
    @Autowired
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    @Override
    protected void checkMsg(TextMsgReq body, Long roomId, Long uid) {
//        检验回复信息的合法性
    }

    @Override
    public void saveMsg(Message msg, TextMsgReq body) {//插入消息实际文本内容
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setContent(body.getContent());
        update.setExtra(extra);
        messageDao.updateById(update);
    }

//    用于构造消息体body   ChatMessageResp.Message   messageVO.setBody(msgHandler.showMsg(message));
    @Override
    public Object showMsg(Message msg) {
        TextMsgResp resp = new TextMsgResp();
        resp.setContent(msg.getContent());
        return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    public String showContactMsg(Message msg) {
        return msg.getContent();
    }
}
