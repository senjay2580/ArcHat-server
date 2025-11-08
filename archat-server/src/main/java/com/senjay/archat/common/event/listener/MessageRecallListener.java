package com.senjay.archat.common.event.listener;


import com.senjay.archat.common.chat.dao.GroupMemberDao;
import com.senjay.archat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.senjay.archat.common.chat.service.ChatService;
import com.senjay.archat.common.chat.service.WebSocketService;
import com.senjay.archat.common.event.MessageRecallEvent;
import com.senjay.archat.common.service.PushService;
import com.senjay.archat.common.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 消息撤回监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class MessageRecallListener {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private PushService pushService;

//    @Async
//    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
////  异步调用 evictMsg() 方法,去将这条消息 msgId 从缓存中清除。
//
//    public void evictMsg(MessageRecallEvent event) {
//        ChatMsgRecallDTO recallDTO = event.getRecallDTO();
//        msgCache.evictMsg(recallDTO.getMsgId());
//    }

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void sendToAll(MessageRecallEvent event) {
        ChatMsgRecallDTO recallDTO = event.getRecallDTO();
        pushService.sendPushMsg(WSAdapter.buildMsgRecall(recallDTO),groupMemberDao.getAllMemberUid(recallDTO.getRoomId()));
    }

}
