package com.senjay.archat.common.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.senjay.archat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.senjay.archat.common.chat.domain.enums.WSRespTypeEnum;
import com.senjay.archat.common.chat.domain.vo.response.ChatMemberResp;
import com.senjay.archat.common.chat.domain.vo.response.ChatMemberStatisticResp;
import com.senjay.archat.common.chat.domain.vo.response.group.ChatMessageResp;
import com.senjay.archat.common.chat.domain.vo.response.ws.WSBaseResp;
import com.senjay.archat.common.chat.domain.vo.response.ws.WSLoginSuccess;
import com.senjay.archat.common.chat.domain.vo.response.ws.WSMsgRecall;
import com.senjay.archat.common.chat.service.ChatService;
import com.senjay.archat.common.service.ChatMemberHelper;
import com.senjay.archat.common.user.domain.entity.User;
import com.senjay.archat.common.user.domain.enums.UserStatusEnum;
import com.senjay.archat.common.user.domain.vo.response.ws.WSOnlineOfflineNotify;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
//它相当于一个“数据适配器”或“消息模板工厂”，用于把 Java 中的业务数据（比如用户对象、状态等）转换成统一格式的 WebSocket 响应对象 WSBaseResp<T>，然后你可以通过 WebSocket 发给前端。
public class WSAdapter {
    private final ChatMemberHelper chatMemberHelper;

    public static WSBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp msgResp) {
        WSBaseResp<ChatMessageResp> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MESSAGE.getType());
        wsBaseResp.setData(msgResp);
        return wsBaseResp;
    }

    public static WSBaseResp<WSLoginSuccess> buildInvalidateTokenResp() {
        WSBaseResp<WSLoginSuccess> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildMsgRecall(ChatMsgRecallDTO recallDTO) {
        WSBaseResp<WSMsgRecall> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.RECALL.getType());
        WSMsgRecall recall = new WSMsgRecall();
        BeanUtils.copyProperties(recallDTO, recall);
        wsBaseResp.setData(recall);
        return wsBaseResp;
    }

//    用户上下线 ws 消息通知
    public WSBaseResp<WSOnlineOfflineNotify> buildOnlineNotifyResp(User user) {
        WSBaseResp<WSOnlineOfflineNotify> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.ONLINE_OFFLINE_NOTIFY.getType());
        WSOnlineOfflineNotify onlineOfflineNotify = new WSOnlineOfflineNotify();
//        Collections.singletonList(...)
//        用这个单个在线用户信息对象，创建一个只包含这一个元素的不可变列表（List）。
//        这个方法会返回一个长度为1的列表，里面只有这个用户的在线信息。

        onlineOfflineNotify.setChangeList(Collections.singletonList(buildOnlineInfo(user)));
        assembleNum(onlineOfflineNotify);
//        将 在线人数总数 和 新上线的人（列表） 赋给这个对象作为ws消息通知返回
        wsBaseResp.setData(onlineOfflineNotify);
        return wsBaseResp;
    }
    public WSBaseResp<WSOnlineOfflineNotify> buildOfflineNotifyResp(User user) {
        WSBaseResp<WSOnlineOfflineNotify> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.ONLINE_OFFLINE_NOTIFY.getType());
        WSOnlineOfflineNotify onlineOfflineNotify = new WSOnlineOfflineNotify();
        onlineOfflineNotify.setChangeList(Collections.singletonList(buildOfflineInfo(user)));
        assembleNum(onlineOfflineNotify);
        wsBaseResp.setData(onlineOfflineNotify);
        return wsBaseResp;
    }


//    收集统计人数
    private void assembleNum(WSOnlineOfflineNotify onlineOfflineNotify) {
        ChatMemberStatisticResp memberStatistic = chatMemberHelper.getMemberStatistic();
        onlineOfflineNotify.setOnlineNum(memberStatistic.getOnlineNum());
    }
//    构造上线用户的详情信息
    private static ChatMemberResp buildOnlineInfo(User user) {
        ChatMemberResp info = new ChatMemberResp();
        BeanUtil.copyProperties(user, info);
        info.setUid(user.getId());
        info.setActiveStatus(UserStatusEnum.ONLINE .getCode());
        return info;
    }
    private static ChatMemberResp buildOfflineInfo(User user) {
        ChatMemberResp info = new ChatMemberResp();
        BeanUtil.copyProperties(user, info);
        info.setUid(user.getId());
        info.setActiveStatus(UserStatusEnum.OFFLINE.getCode());
        return info;
    }
}
