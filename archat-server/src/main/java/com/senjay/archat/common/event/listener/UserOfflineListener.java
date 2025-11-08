package com.senjay.archat.common.event.listener;


import com.senjay.archat.common.chat.service.WebSocketService;
import com.senjay.archat.common.event.UserOfflineEvent;
import com.senjay.archat.common.service.adapter.WSAdapter;
import com.senjay.archat.common.user.dao.UserDao;
import com.senjay.archat.common.user.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户下线监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class UserOfflineListener {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserDao userDao;

    @Autowired
    private WSAdapter wsAdapter;

    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void saveRedisAndPush(UserOfflineEvent event) {
        User user = event.getUser();
//        userCache.offline(user.getId(), user.getLastOptTime());
        //推送给所有在线用户，该用户下线 不用引入mq 因为下线 原因很多而且经常发送 心跳包之类的
        webSocketService.sendToAllOnline(wsAdapter.buildOfflineNotifyResp(event.getUser()));
    }

//    @Async
//    @EventListener(classes = UserOfflineEvent.class)
//    public void saveDB(UserOfflineEvent event) {
//        User user = event.getUser();
//        User update = new User();
//        update.setId(user.getId());
//        update.setLastOptTime(user.getLastOptTime());
//        update.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
//        userDao.updateById(update);
//    }

}
