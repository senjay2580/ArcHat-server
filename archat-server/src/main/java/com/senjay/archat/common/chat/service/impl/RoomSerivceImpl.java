package com.senjay.archat.common.chat.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.chat.dao.*;
import com.senjay.archat.common.chat.domain.dto.GroupApplyDTO;
import com.senjay.archat.common.chat.domain.dto.GroupAuthDTO;
import com.senjay.archat.common.chat.domain.dto.GroupModifyDTO;
import com.senjay.archat.common.chat.domain.dto.RoomSearchDTO;
import com.senjay.archat.common.chat.domain.entity.*;
import com.senjay.archat.common.chat.domain.enums.RoleEnum;
import com.senjay.archat.common.chat.domain.enums.RoomApplyStatusEnum;
import com.senjay.archat.common.chat.domain.enums.RoomStatusEnum;
import com.senjay.archat.common.chat.domain.enums.RoomTypeEnum;
import com.senjay.archat.common.chat.domain.vo.request.*;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupApplyResp;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupDetailResp;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupMemberSimpleResp;
import com.senjay.archat.common.chat.domain.vo.response.group.MyApplyResp;
import com.senjay.archat.common.chat.mapper.RoomMapper;
import com.senjay.archat.common.chat.service.RoomService;
import com.senjay.archat.common.chat.strategy.Abstract.AbstractGroupOperator;
import com.senjay.archat.common.chat.strategy.factory.GroupOperatorFactory;
import com.senjay.archat.common.exception.RoomException;
import com.senjay.archat.common.exception.errorEnums.CommonErrorEnum;
import com.senjay.archat.common.exception.errorEnums.RoomErrorEnum;
import com.senjay.archat.common.service.RoomHelperService;
import com.senjay.archat.common.user.dao.FriendDao;
import com.senjay.archat.common.user.dao.UserDao;
import com.senjay.archat.common.user.domain.entity.User;
import com.senjay.archat.common.util.AssertUtil;
import com.senjay.archat.common.util.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.senjay.archat.common.constant.RoomConstant.*;

@Service
@RequiredArgsConstructor
public class RoomSerivceImpl implements RoomService {
    private final RoomDao roomDao;
    private final ContactDao contactDao;

    private final RoomFriendDao roomFriendDao;
    private final UserDao userDao;
    // help服务类 （辅助类）
    private final RoomHelperService roomHelperService;
    private final FriendDao friendDao;
    private final RoomGroupDao roomGroupDao;
    private final GroupMemberDao groupMemberDao;
    private final GroupApplyDao groupApplyDao;
    private final RoomMapper roomMapper;
    private final RedisTemplate redisTemplate;


    @Override
    public Boolean checkPrivateRoom(Long fid) {
        Long uid = UserHolder.get().getId();
        long min = Math.min(uid, fid);
        long max = Math.max(uid, fid);
        RoomFriend roomFriend = roomFriendDao.lambdaQuery()
                .eq(RoomFriend::getRoomKey, min + "_" + max)
                .eq(RoomFriend::getStatus, RoomStatusEnum.AVAILABLE.getCode())
                .one();
        return roomFriend != null;
    }

    @Override
    public void createPrivateRoom(AdminCreatePrivateRoomReq createPrivateRoomReq) {
        Long uid1 = createPrivateRoomReq.getUid1();
        Long uid2 = createPrivateRoomReq.getUid2();


        if (uid1 != null && uid2 != null) {
            long min = Math.min(uid1, uid2);
            long max = Math.max(uid1, uid2);
            uid1 = min;
            uid2 = max;
        }

//        TODO：查询room_key 看看是不是重复了
        roomHelperService.createRoomFlow(uid1, uid2);
        return;
    }

    @Override
    public void convertStatus(StatusConvertReq statusConvertReq) {
        Long roomId = statusConvertReq.getRoomId();
        Integer status = statusConvertReq.getStatus();
        roomFriendDao.convertStatus(roomId, status);
        return;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePrivateRoom(Integer id) {
        roomDao.removeById(id);
        roomFriendDao.removeRoom(id);
        return;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delBatchOnPrivate(List<Long> ids) {
        roomDao.removeByIds(ids);
        roomFriendDao.deleteBatch(ids);
        return;
    }

    @Override
    public Page<RoomFriend> listRoom(RoomSearchDTO roomSearchDTO) {

        return roomFriendDao.queryPage(roomSearchDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPrivateRoom(ClientCreatePrivateRoomReq createPrivateRoomReq) {
        Long uid2 = createPrivateRoomReq.getUid();
        User user2 = userDao.lambdaQuery().eq(User::getId, uid2).one();
        if (user2 == null) {
            throw new RoomException(RoomErrorEnum.ROOM_MEMBER_NOT_FOUND);
        }
        RoomFriend roomFriend = null;

        Long uid1 = UserHolder.get().getId();
        long min = Math.min(uid1, uid2);
        long max = Math.max(uid1, uid2);
        uid1 = min;
        uid2 = max;
        roomFriend = roomFriendDao.lambdaQuery().eq(RoomFriend::getRoomKey, uid1 + "_" + uid2).one();
        boolean isAvailableRoom = roomFriend != null && RoomStatusEnum.AVAILABLE.getCode().equals(roomFriend.getStatus());
        boolean isForbiddenRoom = roomFriend != null && RoomStatusEnum.FORBIDDEN.getCode().equals(roomFriend.getStatus());
        if (isAvailableRoom) {
            throw new RoomException(RoomErrorEnum.ROOM_ALREADY_EXISTS);
        }
        if (roomFriend == null) {
            roomHelperService.createRoomFlow(uid1, uid2);

        } else {
            roomFriend.setStatus(RoomStatusEnum.AVAILABLE.getCode());
            roomFriendDao.updateById(roomFriend);
            contactDao.createContact(uid1, uid2, roomFriend.getRoomId());
        }


    }

    //    @Transactional 是通过 代理对象（Proxy） 来实现事务控制的。
//    这里是用户加入的逻辑不是发起用户加入群聊申请的服务
    @Override
    public void joinGroup(GroupAuthDTO groupAuthDTO) {
        // group_member -> contact
        Long roomId = groupAuthDTO.getRoomId();
        Long uid = groupAuthDTO.getUid();
        Integer role = groupAuthDTO.getRole();
        GroupMember groupMember = GroupMember.builder()
                .uid(uid)
                .role(role)
                .groupId(roomId)
                .build();
        groupMemberDao.save(groupMember);
        contactDao.save(contactDao.buildContact(uid, roomId));
    }

    @Override
    public void modifyApplyMsg(ModifyApplyMsgReq modifyApplyMsgReqs) {
        Long id = modifyApplyMsgReqs.getId();
        String msg = modifyApplyMsgReqs.getMsg();
        groupApplyDao.lambdaUpdate()
                .eq(GroupApply::getId, id)
                .set(GroupApply::getMsg, msg)
                .update();

    }

    /**
     * 群聊相关
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGroupRoom(CreateGroupRoomReq createGroupRoomReq) {
//        room -> room_group
        Room room = Room.builder().build();
        BeanUtils.copyProperties(createGroupRoomReq, room);
        room.setType(RoomTypeEnum.GROUP.getType());
        roomDao.save(room);
        Long roomId = room.getId();
        RoomGroup roomGroup = new RoomGroup();
        roomGroup.setRoomId(roomId);
        BeanUtils.copyProperties(createGroupRoomReq, roomGroup);
        roomGroupDao.save(roomGroup);

        Long uid = UserHolder.get().getId();
        GroupAuthDTO groupAuthDTO = GroupAuthDTO.builder()
                .roomId(roomId)
                .uid(uid)
                .role(RoleEnum.GROUP_OWNER.getCode())
                .build();
//        添加群成员到群聊中
        joinGroup(groupAuthDTO);
    }

    @Override
    public IPage<GroupDetailResp> searchGroup(BasePageReq basePageReq, String keyword) {
        Page<GroupDetailResp> pages = new Page<>(basePageReq.getPage(), basePageReq.getPageSize());
        return roomMapper.searchGroup(pages, keyword);
    }

    @Override
    public GroupDetailResp getGroupDetail(Long roomId) {
        RoomGroup roomGroup = roomGroupDao.lambdaQuery().eq(RoomGroup::getRoomId, roomId).one();
        GroupDetailResp groupDetailResp = new GroupDetailResp();
        BeanUtils.copyProperties(roomGroup, groupDetailResp);

        return groupDetailResp;

    }


    @Override
    public void applyJoinGroup(GroupApplyDTO groupApplyDTO) {
        Long roomId = groupApplyDTO.getRoomId();
        Long uid = UserHolder.get().getId();
        String msg = groupApplyDTO.getMsg();
        RoomGroup roomGroup = roomGroupDao.lambdaQuery().eq(RoomGroup::getRoomId, roomId).one();
        boolean isGroupNotDismissed = roomGroup != null && Objects.equals(roomGroup.getDeleteStatus(), RoomStatusEnum.AVAILABLE.getCode());
        if (!isGroupNotDismissed) {
            throw new RoomException(RoomErrorEnum.ROOM_NOT_FOUND);
        }
        boolean isInGroup = groupMemberDao.lambdaQuery().eq(GroupMember::getUid, uid)
                .eq(GroupMember::getGroupId, roomId)
                .one() != null;
        if (isInGroup) {
            throw new RoomException(RoomErrorEnum.ROOM_ALREADY_JOINED);
        }

        GroupApply existedApply = groupApplyDao.lambdaQuery().eq(GroupApply::getRoomId, roomId)
                .eq(GroupApply::getUid, uid)
                .one();

        if (existedApply == null) {
            existedApply = new GroupApply();
            existedApply.setRoomId(roomId);
            existedApply.setUid(uid);
        }
        existedApply.setMsg(msg);
        existedApply.setStatus(RoomApplyStatusEnum.PENDING.getCode());
        existedApply.setUpdateTime(LocalDateTime.now());
        groupApplyDao.saveOrUpdate(existedApply);
    }

    @Override
    public IPage<GroupApplyResp> listJoinApply(BasePageReq basePageReq) {
        Page<GroupApply> pages = new Page<>(basePageReq.getPage(), basePageReq.getPageSize());
        Long uid = UserHolder.get().getId();
        return roomMapper.listJoinApply(pages, uid);
    }

    @Override
    public IPage<MyApplyResp> listMyApply(BasePageReq basePageReq) {
        Page<GroupApply> pages = new Page<>(basePageReq.getPage(), basePageReq.getPageSize());
        Long uid = UserHolder.get().getId();
        return roomMapper.listMyApply(pages, uid);
    }

    @Override
    public IPage<GroupMemberSimpleResp> listGroupMember(Long roomId, BasePageReq basePageReq) {
        Page<GroupMemberSimpleResp> pages = new Page<>(basePageReq.getPage(), basePageReq.getPageSize());
        return roomMapper.getGroupMember(pages, roomId);
    }

    @Override
    public Long countMember(Long roomId) {
        return roomHelperService.getMemberCount(roomId);
    }

    @Override
    public void applyDealWith(HandleApplyReq handleApplyReq) {
        GroupApply groupApply = groupApplyDao.getById(handleApplyReq.getId());
        if (RoomApplyStatusEnum.PENDING.getCode().equals(groupApply.getStatus())) {
            Integer status = handleApplyReq.getStatus();
            Long roomId = groupApply.getRoomId();
            Long uid = groupApply.getUid();
            groupApply.setStatus(handleApplyReq.getStatus());
            if (RoomApplyStatusEnum.ADMIT.getCode().equals(status)) {
                // 加入群聊
                Long curGroupMemberCount = Long.valueOf(roomHelperService.getMemberCount(roomId));
                if (MAX_MEMBER_COUNT.equals(curGroupMemberCount)) {
                    throw new RoomException(RoomErrorEnum.ROOM_MEMBER_LIMIT_REACHED);
                }
                GroupMember groupMember = GroupMember.builder()
                        .groupId(roomId)
                        .uid(uid)
                        .role(RoleEnum.MEMBER.getCode())
                        .build();
                groupMemberDao.save(groupMember);
                Contact contact = contactDao.buildContact(uid, roomId);
                contactDao.save(contact);
                roomHelperService.changeMemberCount(curGroupMemberCount, INCREMENT_MEMBER_COUNT, roomId);
            }

            groupApplyDao.updateById(groupApply);
        }
    }

//    校验都在这层 策略与机制分离
    @Override
    public void dismissGroup(Long roomId) {
        AbstractGroupOperator operator = getOperator(roomId);
        operator.removeGroup(roomId);
        roomHelperService.clearUpMember(roomId);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void outGroup(Long roomId) {
//        获取的时候 判断房间是否有效 --> 用户是否在房间中
        AbstractGroupOperator operator = getOperator(roomId);
        operator.outGroup(roomId);
        roomHelperService.changeMemberCount(roomHelperService.getMemberCount(roomId), DECREMENT_MEMBER_COUNT, roomId);
    }

    @Override
    public void removeMember(UserAndRoomReq removeMemberReq) {
//        fromUid : 操作员
        Long fromUid = UserHolder.get().getId();
//        被操作对象
        Long toUid = removeMemberReq.getUid();
//        操作空间
        Long roomId = removeMemberReq.getRoomId();
        Integer roleCode = checked(roomId, fromUid, toUid,true);
        AbstractGroupOperator operator = GroupOperatorFactory.getOperator(RoleEnum.of(roleCode));
        operator.removeMember(roomId, toUid);
        roomHelperService.changeMemberCount(roomHelperService.getMemberCount(roomId), DECREMENT_MEMBER_COUNT, roomId);

    }

    @Override
    public void inviteToBeMember(UserAndRoomReq userAndRoomReq) {
        Long roomId = userAndRoomReq.getRoomId();
        Long toUid = userAndRoomReq.getUid();
        Long fromUid = UserHolder.get().getId();
        Integer roleCode = checked(roomId, fromUid, toUid,false);
        AbstractGroupOperator operator = GroupOperatorFactory.getOperator(RoleEnum.of(roleCode));
        operator.inviteMember(roomId, toUid);

    }

    @Override
    public void modifyGroup(GroupModifyDTO groupModifyDTO) {
        Long roomId = groupModifyDTO.getRoomId();
        AbstractGroupOperator operator = getOperator(roomId);
        operator.modifyGroup(groupModifyDTO);
    }

    @Override
    public void authorizeMember(HandleAuthReq handleAuthReq) {
        Long toUid = handleAuthReq.getUid();
        Long formUid = UserHolder.get().getId();
        Long roomId = handleAuthReq.getGroupId();
        Integer roleCode = checked(roomId, formUid, toUid, true);
        AbstractGroupOperator operator = GroupOperatorFactory.getOperator(RoleEnum.of(roleCode));
        operator.authorizeMember(handleAuthReq);
    }
    //region TODOs:公用方法

    /**
     * 检验房间 顺便返回操作用户的等级
     * @param roomId
     * @return
     */
    private AbstractGroupOperator getOperator(Long roomId) {
        if (!roomGroupDao.checkGroupIsExist(roomId)) {
            throw new RoomException(RoomErrorEnum.ROOM_NOT_FOUND);
        }
        Integer roleCode = groupMemberDao.getMember(roomId, UserHolder.get().getId()).getRole();
        AssertUtil.isNotEmpty(roleCode, CommonErrorEnum.PARAM_VALID);
        return GroupOperatorFactory.getOperator(RoleEnum.of(roleCode));
    }
    /**
     * 检验房间和用户  顺便返回操作用户的等级
     * @param roomId
     * @param fromUid
     * @param toUid
     * @return
     */
    private Integer checked(Long roomId, Long fromUid, Long toUid, Boolean expectUserInGroup) {
        if (!roomGroupDao.checkGroupIsExist(roomId)) {
            throw new RoomException(RoomErrorEnum.ROOM_NOT_FOUND);
        }
        List<Long> uids = List.of(fromUid, toUid);
        Map<Long, Integer> uidRoleMap = groupMemberDao.getMemberUidRoleMap(roomId, uids);
        // 实际是否在群里
        boolean actualUserInGroup = uidRoleMap.containsKey(toUid);
        if (!expectUserInGroup.equals(actualUserInGroup)) {
            throw new RoomException(
                    expectUserInGroup
                            ? RoomErrorEnum.ROOM_MEMBER_NOT_FOUND
                            : RoomErrorEnum.ROOM_ALREADY_JOINED
            );
        }
// 校验 fromUid 是否在群中，有权限邀请他人
        if (!uidRoleMap.containsKey(fromUid)) {
            throw new RoomException(RoomErrorEnum.ROOM_NO_PERMISSION);
        }
        return uidRoleMap.get(fromUid);
    }
//endregion

}
