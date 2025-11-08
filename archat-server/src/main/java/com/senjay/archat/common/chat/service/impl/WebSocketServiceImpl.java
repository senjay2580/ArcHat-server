package com.senjay.archat.common.chat.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.senjay.archat.common.chat.domain.dto.WSChannelExtraDTO;
import com.senjay.archat.common.chat.domain.vo.request.WSAuthorize;
import com.senjay.archat.common.chat.domain.vo.response.ws.WSBaseResp;
import com.senjay.archat.common.chat.service.WebSocketService;
import com.senjay.archat.common.config.MutiThread.ThreadPoolConfig;
import com.senjay.archat.common.event.UserOfflineEvent;
import com.senjay.archat.common.event.UserOnlineEvent;
import com.senjay.archat.common.properties.JwtProperties;
import com.senjay.archat.common.service.WebSocketHelper;
import com.senjay.archat.common.service.adapter.WSAdapter;
import com.senjay.archat.common.user.dao.UserDao;
import com.senjay.archat.common.user.domain.entity.User;
import com.senjay.archat.common.user.domain.enums.UserStatusEnum;
import com.senjay.archat.common.user.domain.vo.request.RequestInfo;
import com.senjay.archat.common.util.JwtUtil;
import com.senjay.archat.common.websocket.NettyUtil;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * WebSocket服务实现类
 * <p>
 * 处理WebSocket连接的生命周期管理，包括连接建立、用户认证、消息发送等功能。
 * 维护在线用户列表和连接状态，支持多设备同时在线。
 * </p>
 *
 * @author senjay
 * @since 1.0.0
 */
@Slf4j
@Service
public class WebSocketServiceImpl implements WebSocketService {
    /**
     * 所有已连接的websocket连接列表和一些额外参数
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();
    /**
     * 所有在线的用户和对应的socket
     * 记录每个在线用户的所有连接（因为一个用户可能在多个设备上登录）。
     */

    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();
    @Autowired
    private UserDao userDao;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private WebSocketHelper webSocketHelper;
    @Autowired
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public static ConcurrentHashMap<Channel, WSChannelExtraDTO> getOnlineMap() {
        return ONLINE_WS_MAP;
    }

    /**
     * 处理登录请求
     *
     * @param channel WebSocket连接通道
     */
    @Override
    public void handleLoginReq(Channel channel) {
        log.debug("当前在线连接数: {}", ONLINE_WS_MAP.size());
    }

    /**
     * 处理连接移除事件
     *
     * @param channel 被移除的WebSocket连接通道
     */
    @Override
    public void removed(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        // 从该 DTO 中提取 uid，封装成 Optional 是为了避免 null。
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);
        // 返回值 offlineAll 表示：当前用户是否所有连接都断开了（即彻底下线）
        boolean offlineAll = offline(channel, uidOptional);
        // 如果这个连接属于某个已登录的用户，并且这个用户的所有连接都已下线，
        // 就说明：该用户完全离线了。
        if (uidOptional.isPresent() && offlineAll) {
            User user = new User();
            user.setId(uidOptional.get());
            // 触发 Spring 的事件机制，发布一个用户下线事件（UserOfflineEvent），用于：
            // 通知业务系统用户已离线
            // 可做清理缓存、状态同步等处理
            applicationEventPublisher.publishEvent(new UserOfflineEvent(this, user));
        }
    }

    /**
     * 用户下线处理
     * <p>
     * uid 会可能为空，因为连接刚建好或未登录时，还没有对应的用户ID。
     * </p>
     *
     * @param channel     WebSocket连接通道
     * @param uidOptional 用户ID的Optional封装
     * @return 是否全下线成功
     */
    private boolean offline(Channel channel, Optional<Long> uidOptional) {
        ONLINE_WS_MAP.remove(channel);
        // uidOptional.isPresent() 是用来判断当前 Optional 是否有值（即当前连接是否绑定了用户ID）。
        // 避免直接调用 uidOptional.get() 时抛出异常。
        if (uidOptional.isPresent()) {
            // 可以开一个线程去调整用户的在线状态
            userDao.lambdaUpdate().set(User::getStatus, UserStatusEnum.OFFLINE.getCode())
                    .eq(User::getId, uidOptional.get())
                    .update();
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(ch -> Objects.equals(ch, channel));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
    }

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    @Override
    public void authorize(Channel channel, WSAuthorize wsAuthorize) {
        //校验token
        boolean verifySuccess = webSocketHelper.verify(wsAuthorize.getToken());
        // 用户校验成功给用户登录
        if (verifySuccess) {
//            注意流程 不能从userholder中拿取id
            String token = wsAuthorize.getToken();
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Object userInfoObj = claims.get("userInfo");
            RequestInfo userInfo = BeanUtil.toBean(userInfoObj, RequestInfo.class);
            Long uid = userInfo.getId();
            User user = userDao.getById(uid);
            loginSuccess(channel, user, wsAuthorize.getToken());
        } else { //让前端的token失效;
            sendMsg(channel, WSAdapter.buildInvalidateTokenResp());
        }
    }

    /**
     * 给本地channel发送消息
     *
     * @param channel
     * @param wsBaseResp
     */
    private void sendMsg(Channel channel, WSBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }


    /**
     * 用户上线
     */
    private void online(Channel channel, Long uid) {
        // 在这里设置了 UID
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtil.setAttr(channel, NettyUtil.UID, uid);
    }

    /**
     * 如果在线列表不存在，就先把该channel放进在线列表
     *
     * @param channel
     * @return
     */
    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO =
                ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
    }

    /**
     * (channel必在本地)登录成功，并更新状态
     */
    private void loginSuccess(Channel channel, User user, String token) {
        //更新上线列表
        userDao.lambdaUpdate().set(User::getStatus, UserStatusEnum.ONLINE.getCode())
                .eq(User::getId, user.getId())
                .update();
//        发布事件
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        online(channel, user.getId());
//        //返回给用户登录成功
//        boolean hasPower = iRoleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER);
//        //发送给对应的用户
//        sendMsg(channel, WSAdapter.buildLoginSuccessResp(user, token, hasPower));
//        //发送用户上线事件
//        boolean online = userCache.isOnline(user.getId());
//        if (!online) {
//            user.setLastOptTime(new Date());
//            user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
//            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
//        }
    }

    // 发送消息
    @Override
    public void sendToUid(WSBaseResp<?> wsBaseResp, Long uid) {
        // 遍历用户的所有连接（多个设备）。
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);

        if (CollectionUtil.isEmpty(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        // 默认一个设备
        sendMsg(channels.get(0), wsBaseResp);
        // TODO: 线程池
//        channels.forEach(channel -> {
////            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
//        });
    }

    /**
     * 向所有在线用户推送 WebSocket 消息，并可选跳过某个用户（skipUid），同时采用线程池异步发送，避免阻塞主线程。
     * @param wsBaseResp 发送的消息体
     * @param skipUid    需要跳过的人
     */
    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            if (Objects.nonNull(skipUid) && Objects.equals(ext.getUid(), skipUid)) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp) {
        // 交由一个方法处理
        sendToAllOnline(wsBaseResp, null);
    }
}
