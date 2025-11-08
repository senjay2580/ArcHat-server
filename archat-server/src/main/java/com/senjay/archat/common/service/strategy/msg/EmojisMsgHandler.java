//package com.senjay.archat.common.service.Strategy.msg;
//
//
//import com.senjay.archat.common.chat.dao.MessageDao;
//import com.senjay.archat.common.chat.domain.entity.Message;
//import com.senjay.archat.common.chat.domain.enums.MessageTypeEnum;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//public class EmojisMsgHandler extends AbstractMsgHandler<EmojisMsgDTO> {
//    @Autowired
//    private MessageDao messageDao;
//
//    @Override
//    MessageTypeEnum getMsgTypeEnum() {
//        return MessageTypeEnum.EMOJI;
//    }
//
//    @Override
//    public void saveMsg(Message msg, EmojisMsgDTO body) {
//        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
//        Message update = new Message();
//        update.setId(msg.getId());
//        update.setExtra(extra);
//        extra.setEmojisMsgDTO(body);
//        messageDao.updateById(update);
//    }
//
//    @Override
//    public Object showMsg(Message msg) {
//        return msg.getExtra().getEmojisMsgDTO();
//    }
//
//    @Override
//    public Object showReplyMsg(Message msg) {
//        return "表情";
//    }
//
//    @Override
//    public String showContactMsg(Message msg) {
//        return "[表情包]";
//    }
//}
