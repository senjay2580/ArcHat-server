package com.senjay.archat.common.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.chat.dao.ContactDao;
import com.senjay.archat.common.chat.dao.RoomFriendDao;
import com.senjay.archat.common.chat.domain.entity.Contact;
import com.senjay.archat.common.chat.domain.entity.RoomFriend;
import com.senjay.archat.common.chat.domain.enums.RoomStatusEnum;
import com.senjay.archat.common.chat.domain.enums.WSChatRespTypeEnum;
import com.senjay.archat.common.chat.domain.vo.response.ChatResponse;
import com.senjay.archat.common.chat.domain.vo.response.ws.WSBaseResp;
import com.senjay.archat.common.chat.service.WebSocketService;
import com.senjay.archat.common.exception.UserException;
import com.senjay.archat.common.user.dao.FriendDao;
import com.senjay.archat.common.user.dao.UserDao;
import com.senjay.archat.common.user.domain.dto.FriendApplyQueryDTO;
import com.senjay.archat.common.user.domain.dto.FriendDTO;
import com.senjay.archat.common.user.domain.dto.FriendSearchDTO;
import com.senjay.archat.common.user.domain.entity.Friend;
import com.senjay.archat.common.user.domain.enums.FriendStatusEnum;
import com.senjay.archat.common.user.domain.enums.FriendTipsEnum;
import com.senjay.archat.common.user.domain.vo.request.FriendApplyDealWithReq;
import com.senjay.archat.common.user.domain.vo.request.FriendApplyReq;
import com.senjay.archat.common.user.domain.vo.request.FriendSearchReq;
import com.senjay.archat.common.user.domain.vo.response.FriendApplyResp;
import com.senjay.archat.common.user.domain.vo.response.FriendSearchResp;
import com.senjay.archat.common.user.mapper.FriendMapper;
import com.senjay.archat.common.user.mapper.UserMapper;
import com.senjay.archat.common.user.service.FriendService;
import com.senjay.archat.common.util.UserHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 好友关系服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendDao friendDAO;
    private final UserDao userDao;
    private final UserMapper userMapper;
    private final FriendMapper friendMapper;
    private final RoomFriendDao roomFriendDao;
    private final WebSocketService webSocketService;
    private final ContactDao contactDao;


    /**
     * 发送添加好友申请
     *
     * @param friendApplyReq 好友申请请求参数
     * @return 操作结果提示
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addFriend(FriendApplyReq friendApplyReq) {
        Long uid = UserHolder.get().getId();
        Long fid = friendApplyReq.getFriendId();

        Friend friend = friendDAO.selectByUserAndFriendId(uid, fid);

        if (friend != null) {
            if (FriendStatusEnum.APPROVE.getCode().equals(friend.getStatus())) {
                throw new UserException(FriendTipsEnum.HAD_FRIEND.getDesc());
            }

            if (FriendStatusEnum.PENDING.getCode().equals(friend.getStatus())) {
                throw new UserException(FriendTipsEnum.REPEAT_CLICK_ERROR.getDesc());
            }

            // 如果是拒绝就先删除然后重新添加 (单向删除)
            friendDAO.remove(
                    new LambdaQueryWrapper<Friend>()
                            .eq(Friend::getFriendId, fid)
                            .eq(Friend::getUserId, uid)
            );
        }

        // 添加逻辑
        friend = createFriend(uid, fid, FriendStatusEnum.PENDING.getCode());
        friendDAO.save(friend);

        // 可以添加消息队列 或者多线程优化
        sendFriendApplyTips(uid,fid);

        return FriendTipsEnum.SEND_APPLY.getDesc();
    }

    private void sendFriendApplyTips(Long uid, Long fid) {
        WSBaseResp<ChatResponse> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSChatRespTypeEnum.APPLYTIPS.getType());
        ChatResponse chatResponse = ChatResponse.builder()
                .fromUid(uid)
                .content("")
                .build();
        wsBaseResp.setData(chatResponse);
        webSocketService.sendToUid(wsBaseResp, fid);
    }



    /**
     * 处理好友申请（同意/拒绝）
     *
     * @param friendApplyDealWithReq 处理好友申请请求参数
     * @return 操作结果提示
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String approveOrNot(FriendApplyDealWithReq friendApplyDealWithReq) {
        Long uid = UserHolder.get().getId();
        Long fid = friendApplyDealWithReq.getFriendId();
        Integer status = friendApplyDealWithReq.getStatus();
        // 查询对方发来的好友申请
        Friend friend = friendDAO.lambdaQuery()
                .eq(Friend::getUserId, fid)
                .eq(Friend::getFriendId, uid)
                .one();
        if (friend == null) {
            throw new UserException(FriendTipsEnum.NOT_EXIST_FRIEND.getDesc());

        }
        if (FriendStatusEnum.APPROVE.getCode().equals(friend.getStatus())) {
            throw new UserException(FriendTipsEnum.HAD_FRIEND.getDesc());
        }
        if (status.equals(FriendStatusEnum.REJECT.getCode())) {
            if (FriendStatusEnum.REJECT.getCode().equals(friend.getStatus())) {
                throw new UserException(FriendTipsEnum.REPEAT_CLICK_ERROR.getDesc());
            }
            friendDAO.update(
                    new LambdaUpdateWrapper<Friend>()
                            .eq(Friend::getId, friend.getId())
                            .set(Friend::getStatus, status)
            );
            return FriendTipsEnum.REJECT_APPLY.getDesc();
        }
        deleteFriend(uid, fid);
        return approveFriend(uid, fid);
    }


    /**
     * 创建并添加好友关系
     *
     * @param friendDTO 好友关系DTO
     * @return 操作结果提示
     */
    @Override
    public String createAndAdd(FriendDTO friendDTO) {
        Long uid = friendDTO.getUserId();
        Long fid = friendDTO.getFriendId();

        if (uid.equals(fid)) {
            return FriendTipsEnum.APPLY_ONSELF_ERROR.getDesc();
        }

        Integer status = friendDTO.getStatus();
        if (FriendStatusEnum.APPROVE.getCode().equals(status)) {
            Friend friend1 = createFriend(uid, fid, FriendStatusEnum.APPROVE.getCode());
            Friend friend2 = createFriend(fid, uid, FriendStatusEnum.APPROVE.getCode());
            friendDAO.saveBatch(List.of(friend1, friend2));
            return FriendTipsEnum.ADD_SUCCESS.getDesc();
        }

        Friend friend = createFriend(uid, fid, status);
        friendDAO.save(friend);
        return FriendTipsEnum.INSERT_FRIENDSHIP.getDesc();
    }

    /**
     * 删除好友关系（通过用户ID和好友ID）
     *
     * @param uid 用户ID
     * @param fid 好友ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(Long uid, Long fid) {
        List<Friend> userFriend = friendDAO.getUserFriend(uid, fid);
        if (userFriend.isEmpty()) {
            log.info(FriendTipsEnum.NOT_EXIST_FRIEND.getDesc());
            return;
        }

        List<Long> userFriendIds = userFriend.stream()
                .map(Friend::getId)
                .collect(Collectors.toList());

        friendDAO.removeByIds(userFriendIds);
        String roomKey = getRoomKey(uid, fid);
        RoomFriend roomFriend = roomFriendDao.lambdaQuery().eq(RoomFriend::getRoomKey, roomKey)
                .one();
        if(roomFriend != null) {
            roomFriend.setStatus(RoomStatusEnum.FORBIDDEN.getCode());
            roomFriendDao.updateById(roomFriend);
            //        删除contact 将好友双方的会话都删除了
            contactDao.remove(new LambdaUpdateWrapper<Contact>().eq(Contact::getRoomId, roomFriend.getRoomId()));
        }



    }
    private static String getRoomKey(Long uid, Long fid) {
        Long minId = Math.min(uid, fid);
        Long maxId = Math.max(uid, fid);
        return minId + "_" + maxId;
    }

    /**
     * 删除好友关系（通过好友ID）
     *
     * @param friendId 好友ID
     * @return 操作结果提示
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteFriend(Long friendId) {
        Long uid = UserHolder.get().getId();
        Friend friend = friendDAO.selectByUserAndFriendId(uid, friendId);

        if (friend == null) {
            return FriendTipsEnum.NOT_EXIST_FRIEND.getDesc();
        }

        if (FriendStatusEnum.APPROVE.getCode().equals(friend.getStatus())) {
            // 双向删除
            deleteFriend(uid, friendId);
            return FriendTipsEnum.DELETE_SUCCESS.getDesc();
        }

        friendDAO.remove(
                new LambdaQueryWrapper<Friend>()
                        .eq(Friend::getFriendId, friendId)
                        .eq(Friend::getUserId, uid)
        );

        RoomFriend roomFriend = roomFriendDao.lambdaQuery().eq(RoomFriend::getRoomKey, getRoomKey(uid, friendId))
                .one();
        if(roomFriend != null) {
            roomFriend.setStatus(RoomStatusEnum.FORBIDDEN.getCode());
            roomFriendDao.updateById(roomFriend);
        }
        return FriendTipsEnum.REMOVE_SUCCESS.getDesc();
    }

    /**
     * 根据记录ID删除好友记录
     *
     * @param id 记录ID
     * @return 操作结果提示
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteById(Long id) {
        Friend friend = friendDAO.getById(id);

        if (FriendStatusEnum.APPROVE.getCode().equals(friend.getStatus())) {
            deleteFriend(friend.getUserId(), friend.getFriendId());
            return FriendTipsEnum.DELETE_SUCCESS.getDesc();
        }

        friendDAO.removeById(id);
        return FriendTipsEnum.REMOVE_SUCCESS.getDesc();
    }


    /**
     * 分页查询好友列表
     *
     * @param friendSearchDTO 查询条件
     * @return 分页结果
     */
    @Override
    public Page<Friend> listFriend(FriendSearchDTO friendSearchDTO) {
        return friendDAO.getBySearch(friendSearchDTO);
    }


    /**
     * 同意好友申请
     *
     * @param uid 用户ID
     * @param fid 好友ID
     * @return 操作结果提示
     */
    private String approveFriend(Long uid, Long fid) {
        Friend friend1 = createFriend(uid, fid, FriendStatusEnum.APPROVE.getCode());
        Friend friend2 = createFriend(fid, uid, FriendStatusEnum.APPROVE.getCode());
        friendDAO.saveBatch(List.of(friend1, friend2));
        return FriendTipsEnum.ADD_SUCCESS.getDesc();
    }

    /**
     *
     * @param uid
     * @param fid
     * @param status
     * @return
     */
    private Friend createFriend(Long uid, Long fid, Integer status) {
        return Friend.builder()
                .userId(uid)
                .friendId(fid)
                .status(status)
                .createTime(LocalDateTime.now())
                .build();
    }

    @Override
    public IPage<FriendSearchResp> search(FriendSearchReq friendSearchReq) {
        String keyword = friendSearchReq.getKeyword();
        Integer page = friendSearchReq.getPage();
        Integer pageSize = friendSearchReq.getPageSize();
        Page<FriendSearchResp> friendSearchRespPage = new Page<>(page, pageSize);
        return userMapper.searchFriend(friendSearchRespPage, keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FriendSearchResp> listFriends(Long uid) {
        // 先查询当前用户所有的朋友
        List<Long> friendIds = getFriendIds(uid);
        if (friendIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userMapper.listFriends(friendIds);
    }
    @Override
    public List<Long> getFriendIds(Long uid) {
        List<Friend> friends = friendDAO.lambdaQuery().eq(Friend::getUserId, uid)
                .eq(Friend::getStatus,FriendStatusEnum.APPROVE.getCode())
                .select(Friend::getFriendId)
                .list();
        // 获取这些朋友的id集合
        return friends.stream().map(Friend::getFriendId).collect(Collectors.toList());
    }

    // ... (其他代码保持不变)

    @Override
    public IPage<FriendApplyResp> listMyApply(FriendApplyQueryDTO friendApplyQueryDTO) {
        Integer page = friendApplyQueryDTO.getPage();
        Integer pageSize = friendApplyQueryDTO.getPageSize();
        Long uid = friendApplyQueryDTO.getUid();
        Page<FriendSearchResp> pages = new Page<>(page, pageSize);
        return friendMapper.listApply(pages,uid);

    }

    @Override
    public IPage<FriendApplyResp> listMyReceive(FriendApplyQueryDTO friendApplyQueryDTO) {
        Integer page = friendApplyQueryDTO.getPage();
        Integer pageSize = friendApplyQueryDTO.getPageSize();
        Long uid = friendApplyQueryDTO.getUid();
        Page<FriendSearchResp> pages = new Page<>(page, pageSize);
        return friendMapper.listReceive(pages,uid);
    }

    @Override
    public Boolean checkFriendship(Long fid) {
        Long uId = UserHolder.get().getId();
        Friend friend = friendDAO.lambdaQuery().eq(Friend::getUserId, uId)
                .eq(Friend::getFriendId, fid)
                .eq(Friend::getStatus, FriendStatusEnum.APPROVE.getCode())
                .one();
        return friend != null;

    }
}