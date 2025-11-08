package com.senjay.archat.common.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.enums.MessageStatusEnum;
import com.senjay.archat.common.chat.domain.vo.request.ChatMessageReq;
import com.senjay.archat.common.chat.domain.vo.response.group.ChatMessageResp;
import com.senjay.archat.common.service.Strategy.msg.AbstractMsgHandler;
import com.senjay.archat.common.service.Strategy.msg.MsgHandlerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 消息适配器
 */
public class MessageAdapter {
    public static final int CAN_CALLBACK_GAP_COUNT = 100;

    public static Message buildMsgSave(ChatMessageReq request, Long uid) {

        return Message.builder()
                .fromUid(uid)
                .roomId(request.getRoomId())
                .type(request.getMsgType())
                .status(MessageStatusEnum.NORMAL.getStatus())
                .build();

    }
// 批量 + 单一 统一处理思路 ？Stream流处理方法 TODO
    public static List<ChatMessageResp> buildMsgResp(List<Message> messages) {
        return messages.stream().map(a -> {
                    ChatMessageResp resp = new ChatMessageResp();
                    resp.setFromUser(buildFromUser(a.getFromUid()));
                    resp.setMessage(buildMessage(a));
                    return resp;
                })
                //帮前端排好序，更方便它展示
                .sorted(Comparator.comparing(a -> a.getMessage().getSendTime()))
                .collect(Collectors.toList());
    }

//region 消息响应 的内部类构造器
    private static ChatMessageResp.UserInfo buildFromUser(Long fromUid) {
        ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
        userInfo.setUid(fromUid);
        return userInfo;
    }

    private static ChatMessageResp.Message buildMessage(Message message) {
        ChatMessageResp.Message messageVO = new ChatMessageResp.Message();
        BeanUtil.copyProperties(message, messageVO);
        messageVO.setSendTime(message.getCreateTime());

        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(message.getType());
        if (Objects.nonNull(msgHandler)) {
            messageVO.setBody(msgHandler.showMsg(message));
        }
        return messageVO;
    }

//endregion

}
