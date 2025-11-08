package com.senjay.archat.common.event.listener;
import com.senjay.archat.common.chat.service.WebSocketService;
import com.senjay.archat.common.event.UserOnlineEvent;
import com.senjay.archat.common.service.PushService;
import com.senjay.archat.common.service.adapter.WSAdapter;
import com.senjay.archat.common.user.domain.entity.User;
import com.senjay.archat.common.user.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// 用户上线推送 给在线的所有 好友
public class UserOnlineListener {
    private final WebSocketService webSocketService;
    private final PushService pushService;
    private final FriendService friendService;
    private final WSAdapter wsAdapter;

    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void pushNotice(UserOnlineEvent event) {
        User user = event.getUser();
        pushService.sendPushMsg(wsAdapter.buildOnlineNotifyResp(user));
    }
}
