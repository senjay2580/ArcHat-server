package com.senjay.archat.common.chat.strategy;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.senjay.archat.common.chat.dao.GroupApplyDao;
import com.senjay.archat.common.chat.dao.GroupMemberDao;
import com.senjay.archat.common.chat.dao.RoomGroupDao;
import com.senjay.archat.common.chat.domain.dto.GroupModifyDTO;
import com.senjay.archat.common.chat.domain.entity.GroupApply;
import com.senjay.archat.common.chat.domain.entity.GroupMember;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.entity.RoomGroup;
import com.senjay.archat.common.chat.domain.enums.RoleEnum;
import com.senjay.archat.common.chat.domain.vo.request.HandleAuthReq;
import com.senjay.archat.common.chat.service.impl.RoomGroupHelper;
import com.senjay.archat.common.chat.strategy.Abstract.AbstractGroupOperator;
import com.senjay.archat.common.event.GroupRemovedEvent;
import com.senjay.archat.common.exception.RoomException;
import com.senjay.archat.common.exception.errorEnums.CommonErrorEnum;
import com.senjay.archat.common.exception.errorEnums.RoomErrorEnum;
import com.senjay.archat.common.service.Strategy.msg.RecallMsgHandler;
import com.senjay.archat.common.user.dao.UserDao;
import com.senjay.archat.common.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 群主操作器
 * <p>
 * 实现群主在群组中的所有操作权限，包括邀请成员、移除成员、解散群聊、修改群信息、授权成员等。
 * 群主拥有最高权限，可以执行所有群组操作。
 * </p>
 *
 * @author senjay
 * @since 1.0.0
 */
@Service
@Slf4j
public class OwnerOperator extends AbstractGroupOperator {
    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoomGroupDao roomGroupDao;
    @Autowired
    private GroupApplyDao groupApplyDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private RoomGroupHelper roomGroupHelper;
    @Autowired
    private RecallMsgHandler recallMsgHandler;



    /**
     * 获取角色代码
     *
     * @return 群主角色代码
     */
    @Override
    public Integer getRole() {
        return RoleEnum.GROUP_OWNER.getCode();
    }

    /**
     * 邀请成员加入群聊
     *
     * @param roomId 房间ID
     * @param fid    被邀请用户ID
     */
    @Override
    public void inviteMember(Long roomId, Long fid) {
        GroupMember member = groupMemberDao.getMember(roomId, UserHolder.get().getId());
        String ownerName = userDao.getById(member.getUid()).getUsername();
        String inviteeName = userDao.getById(fid).getUsername();
        log.info("群主[{}]邀请用户[{}]加入群聊[{}]", ownerName, inviteeName, roomId);
    }

    /**
     * 解散群聊
     * <p>
     * 只有群主可以解散群聊，解散后会清理相关数据并发布事件
     * </p>
     *
     * @param roomId 房间ID
     */
    @Override
    public void removeGroup(Long roomId) {
        // roomGroup delete status 字段 删除的时候就会自动修改字段 groupApply
        // groupmember  contact message 删除
        RoomGroup roomGroup = roomGroupDao.lambdaQuery()
                .eq(RoomGroup::getRoomId, roomId)
                .one();
        if(roomGroup == null || roomGroup.getDeleteStatus() == 1) {
            throw new RoomException(RoomErrorEnum.ROOM_NOT_FOUND);
        }
        // 逻辑删除（实体类中约定）
        roomGroupDao.removeById(roomGroup);
        groupApplyDao.remove(new LambdaQueryWrapper<GroupApply>().eq(GroupApply::getRoomId, roomId));
        // 发布群聊事件
        applicationEventPublisher.publishEvent(new GroupRemovedEvent(this, roomId));
    }

    /**
     * 移除群成员
     * <p>
     * 群主可以移除任何成员，但不能移除自己
     * </p>
     *
     * @param roomId 房间ID
     * @param uid    要移除的用户ID
     */
    @Override
    public void removeMember(Long roomId, Long uid) {
        GroupMember groupMember = groupMemberDao.getMember(roomId, uid);
        if(uid.equals(UserHolder.get().getId())) {
            throw new RoomException(CommonErrorEnum.SYSTEM_ERROR);
        }
        roomGroupHelper.clearApplyAndMember(uid, roomId);
    }

    /**
     * 修改群组信息
     * <p>
     * 群主可以修改群名称、群头像、群描述等信息
     * </p>
     *
     * @param groupModifyDTO 群组修改信息
     */
    @Override
    public void modifyGroup(GroupModifyDTO groupModifyDTO) {
//        roomGroupDao.lambdaUpdate()
//                .eq(RoomGroup::getRoomId, groupModifyDTO.getRoomId())
//                .set(StrUtil.isNotBlank(groupModifyDTO.getName()), RoomGroup::getName, groupModifyDTO.getName())
//                .set(StrUtil.isNotBlank(groupModifyDTO.getAvatar()), RoomGroup::getAvatar, groupModifyDTO.getAvatar())
//                .set(StrUtil.isNotBlank(groupModifyDTO.getGroupDesc()), RoomGroup::getGroupDesc, groupModifyDTO.getGroupDesc())
//                .update();
//        注意前端可能会传空字符串
        roomGroupDao.update(new LambdaUpdateWrapper<RoomGroup>()
                .eq(RoomGroup::getRoomId, groupModifyDTO.getRoomId())
                .set(StrUtil.isNotBlank(groupModifyDTO.getName()), RoomGroup::getName, groupModifyDTO.getName())
                .set(StrUtil.isNotBlank(groupModifyDTO.getAvatar()), RoomGroup::getAvatar, groupModifyDTO.getAvatar())
                .set(StrUtil.isNotBlank(groupModifyDTO.getGroupDesc()), RoomGroup::getGroupDesc, groupModifyDTO.getGroupDesc())
        );
    }

    /**
     * 退出群聊
     * <p>
     * 群主不能退出群聊，只能解散群聊
     * </p>
     *
     * @param roomId 房间ID
     */
    @Override
    public void outGroup(Long roomId) {
        throw new RoomException(RoomErrorEnum.ROOM_OWNER_CANNOT_QUIT);
    }

    /**
     * 授权成员
     * <p>
     * 群主可以设置成员为管理员或取消管理员权限
     * </p>
     *
     * @param handleAuthReq 授权请求
     */
    @Override
    public void authorizeMember(HandleAuthReq handleAuthReq) {
        groupMemberDao.lambdaUpdate()
                .eq(GroupMember::getGroupId, handleAuthReq.getGroupId())
                .eq(GroupMember::getUid, handleAuthReq.getUid())
                .set(GroupMember::getRole, handleAuthReq.getRole())
                .update();
    }

    /**
     * 撤回消息
     * <p>
     * 群主可以撤回任何人的消息，无时间限制
     * </p>
     *
     * @param uid     操作用户ID
     * @param message 要撤回的消息
     */
    @Override
    public void recallMessage(Long uid, Message message) {
        checkRecall(message);
        recallMsgHandler.recall(uid, message);
    }

}
