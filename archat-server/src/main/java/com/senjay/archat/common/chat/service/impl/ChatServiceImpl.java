package com.senjay.archat.common.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.senjay.archat.common.chat.dao.GroupMemberDao;
import com.senjay.archat.common.chat.dao.MessageDao;
import com.senjay.archat.common.chat.domain.entity.GroupMember;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.entity.RoomGroup;
import com.senjay.archat.common.chat.domain.enums.MessageTypeEnum;
import com.senjay.archat.common.chat.domain.enums.RoleEnum;
import com.senjay.archat.common.chat.domain.enums.WSChatReqTypeEnum;
import com.senjay.archat.common.chat.domain.enums.WSChatRespTypeEnum;
import com.senjay.archat.common.chat.domain.vo.request.ChatMessageBaseReq;
import com.senjay.archat.common.chat.domain.vo.request.ChatMessageReq;
import com.senjay.archat.common.chat.domain.vo.request.WSBaseReq;
import com.senjay.archat.common.chat.domain.vo.request.WSChatReq;
import com.senjay.archat.common.chat.domain.vo.response.ChatResponse;
import com.senjay.archat.common.chat.domain.vo.response.group.ChatMessageResp;
import com.senjay.archat.common.chat.domain.vo.response.ws.WSBaseResp;
import com.senjay.archat.common.chat.service.ChatService;
import com.senjay.archat.common.chat.service.MessageService;
import com.senjay.archat.common.chat.service.WebSocketService;
import com.senjay.archat.common.chat.strategy.Abstract.AbstractGroupOperator;
import com.senjay.archat.common.chat.strategy.factory.GroupOperatorFactory;
import com.senjay.archat.common.event.MessageSendEvent;
import com.senjay.archat.common.service.Strategy.msg.AbstractMsgHandler;
import com.senjay.archat.common.service.Strategy.msg.MsgHandlerFactory;
import com.senjay.archat.common.service.adapter.MessageAdapter;
import com.senjay.archat.common.util.AssertUtil;
import com.senjay.archat.common.websocket.NettyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 聊天服务实现类
 * <p>
 * 提供聊天相关的核心功能，包括消息处理、发送、撤回等操作。
 * 支持私聊和群聊两种模式的消息处理。
 * </p>
 *
 * @author senjay
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final WebSocketService webSocketService;
    private final MessageService messageService;
    private final GroupMemberDao groupMemberDao;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageDao messageDao;

    /**
     * 处理WebSocket聊天消息
     *
     * @param msg WebSocket文本帧消息
     * @param ctx 通道处理上下文
     */
    @Override
    public void handleChat(TextWebSocketFrame msg, ChannelHandlerContext ctx) {
        WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);

        // 关键修复：data 是 JSON 字符串，需要二次反序列化
        WSChatReq wsChatReq = JSONUtil.toBean(wsBaseReq.getData(), WSChatReq.class);
        WSChatReqTypeEnum wsChatTypeEnum = WSChatReqTypeEnum.of(wsChatReq.getType());

        switch (wsChatTypeEnum) {
            case PRIVATE:
                Long fromUid = NettyUtil.getAttr(ctx.channel(), NettyUtil.UID);
                handlePrivate(fromUid,wsChatReq, wsBaseReq);
                break;
            case GROUP:
                log.info("处理群聊消息");
                break;
        }
    }

    /**
     * 处理私聊消息
     *
     * @param fromUid    发送者用户ID
     * @param wsChatReq  聊天请求对象
     * @param wsBaseReq  基础WebSocket请求对象
     */
    private void handlePrivate(Long fromUid, WSChatReq wsChatReq, WSBaseReq wsBaseReq) {
        Long targetUid = wsChatReq.getTargetUid();
        Long roomId = wsChatReq.getRoomId();
        String content = wsChatReq.getContent();

        messageService.sendTextToUid(fromUid, roomId, content);
        WSBaseResp<ChatResponse> wsBaseResp = new WSBaseResp<>();
        ChatResponse cr = ChatResponse.builder()
                .fromUid(fromUid)
                .content(content)
                .build();
        wsBaseResp.setData(cr);
        wsBaseResp.setType(WSChatRespTypeEnum.TEXT.getType());
        webSocketService.sendToUid(wsBaseResp, targetUid);
    }

    /**
     * 发送消息
     *
     * @param chatMessageReq 聊天消息请求
     * @param uid           发送者用户ID
     * @return 消息ID
     */
    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq chatMessageReq, Long uid) {
        // 先检验是否有权发送消息
        check(chatMessageReq, uid);
        // 通过消息格式类型获取对应的消息处理器（通过注册工厂生成）
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(chatMessageReq.getMsgType());
        // 在这个方法上 将消息保存到了数据库 核心操作
        Long msgId = msgHandler.checkAndSaveMsg(chatMessageReq, uid);
        // 发布消息发送事件 推送给这个消息所在的群聊当中的所有用户
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));
        return msgId;
    }

    /**
     * 撤回消息
     *
     * @param uid     操作用户ID
     * @param request 消息基础请求
     */
    @Override
    public void recallMsg(Long uid, ChatMessageBaseReq request) {
        // 获取原始消息
        Message message = messageDao.getById(request.getMsgId());
        Integer roleCode = groupMemberDao.getMember(request.getRoomId(), uid).getRole();
        AbstractGroupOperator operator = GroupOperatorFactory.getOperator(RoleEnum.of(roleCode));
        operator.recallMessage(uid, message);
    }

    /**
     * 检查用户是否有权限发送消息
     *
     * @param chatMessageReq 聊天消息请求
     * @param uid           用户ID
     */
    private void check(ChatMessageReq chatMessageReq, Long uid) {
        GroupMember member = groupMemberDao.getMember(chatMessageReq.getRoomId(), uid);
        // 断言工具类
        AssertUtil.isNotEmpty(member, "您已经被移除该群");
    }

    /**
     * 获取单个消息的响应对象
     *
     * @param message 消息对象
     * @return 消息响应对象
     */
    @Override
    public ChatMessageResp getMsgResp(Message message) {
        // 安全地获取集合中的第一个元素，如果集合是 null 或为空，则返回 null，避免抛异常。
        return CollUtil.getFirst(getMsgRespBatch(Collections.singletonList(message)));
    }

    /**
     * 根据消息ID获取消息响应对象
     *
     * @param msgId 消息ID
     * @return 消息响应对象
     */
    @Override
    public ChatMessageResp getMsgResp(Long msgId) {
        Message msg = messageDao.getById(msgId);
        return getMsgResp(msg);
    }

    /**
     * 批量获取消息响应对象
     *
     * @param messages 消息列表
     * @return 消息响应对象列表
     */
    public List<ChatMessageResp> getMsgRespBatch(List<Message> messages) {
        if (CollectionUtil.isEmpty(messages)) {
            return new ArrayList<>();
        }

        return MessageAdapter.buildMsgResp(messages);
    }
}