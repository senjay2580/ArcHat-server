package com.senjay.archat.common.event.listener;

import com.senjay.archat.common.chat.domain.dto.RoomMessageDTO;
import com.senjay.archat.common.constant.MQConstant;
import com.senjay.archat.common.event.GroupRemovedEvent;
import com.senjay.archat.common.service.MQService.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collections;

@Slf4j
@Component
public class GroupRemovedListener {
    @Autowired
    private MQProducer mqProducer;
//     问题： 更新数据库 后 消息队列 如何保持事务 使用  Spring 事务事件发布 + 监听器异步消息发送 那要是消息队列的消息消费异常怎么办 消息队列可靠性问题
//    只有当触发事件的方法所在的事务成功提交后，事件监听方法才会被调用执行。
    @TransactionalEventListener(classes = GroupRemovedEvent.class, fallbackExecution = true,phase = TransactionPhase.AFTER_COMMIT)
    public void onGroupRemoved(GroupRemovedEvent event) {
        Long roomId = event.getRoomId();
        mqProducer.sendMsg(MQConstant.UPDATE_EXCHANGE, MQConstant.GROUP_CLEAN_UP_BINDING_KEY, new RoomMessageDTO(Collections.emptyList(), roomId));
    }


}
