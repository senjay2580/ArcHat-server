package com.senjay.archat.common.chat.strategy.Abstract;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.senjay.archat.common.chat.dao.GroupApplyDao;
import com.senjay.archat.common.chat.dao.GroupMemberDao;
import com.senjay.archat.common.chat.domain.dto.GroupModifyDTO;
import com.senjay.archat.common.chat.domain.entity.GroupMember;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.enums.MessageTypeEnum;
import com.senjay.archat.common.chat.domain.enums.RoleEnum;
import com.senjay.archat.common.chat.domain.vo.request.HandleAuthReq;
import com.senjay.archat.common.chat.service.impl.RoomGroupHelper;
import com.senjay.archat.common.chat.strategy.factory.GroupOperatorFactory;
import com.senjay.archat.common.exception.RoomException;
import com.senjay.archat.common.exception.errorEnums.CommonErrorEnum;
import com.senjay.archat.common.exception.errorEnums.RoomErrorEnum;
import com.senjay.archat.common.service.Strategy.msg.RecallMsgHandler;
import com.senjay.archat.common.util.AssertUtil;
import com.senjay.archat.common.util.UserHolder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 群组操作抽象类
 * <p>
 * 定义了群组操作的基本功能，包括邀请成员、移除成员、解散群聊、撤回消息等。
 * 不同角色（群主、管理员、成员）通过继承此类实现各自的权限控制。
 * </p>
 *
 * @author senjay
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractGroupOperator {
    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private GroupApplyDao groupApplyDao;
    @Autowired
    private RoomGroupHelper roomGroupHelper;
    @Autowired
    private RecallMsgHandler recallMsgHandler;


    /**
     * 初始化方法，将当前操作器注册到工厂中
     */
    @PostConstruct
    private void init() {
        GroupOperatorFactory.registerOperator(getRole(), this);
    }

    /**
     * 获取当前操作器对应的角色代码
     *
     * @return 角色代码
     */
    public abstract Integer getRole();


    /**
     * 邀请成员加入群聊
     *
     * @param roomId 房间ID
     * @param fid    被邀请用户ID
     */
    public void inviteMember(Long roomId, Long fid) {
        Long uid = UserHolder.get().getId();
        log.info("用户[{}]邀请用户[{}]加入群聊[{}]", uid, fid, roomId);
    }

        /**
     * 解散群聊
     * <p>
     * 默认实现抛出无权限异常，只有群主可以解散群聊
     * </p>
     *
     * @param roomId 房间ID
     * @throws RoomException 当用户无权限时抛出
     */
    public void removeGroup(Long roomId) {
        throw new RoomException(RoomErrorEnum.ROOM_NO_PERMISSION);
    }


        /**
     * 移除群成员
     * <p>
     * 只能移除普通成员，不能移除管理员或群主。用户不能移除自己。
     * </p>
     *
     * @param roomId 房间ID
     * @param uid    要移除的用户ID
     * @throws RoomException 当用户无权限或操作不当时抛出
     */
    public void removeMember(Long roomId, Long uid) {
        GroupMember groupMember = groupMemberDao.getMember(roomId, uid);
        if(uid.equals(UserHolder.get().getId())) {
            throw new RoomException(CommonErrorEnum.SYSTEM_ERROR);
        }
        Integer roleCode = groupMember.getRole();
        if(!roleCode.equals(RoleEnum.MEMBER.getCode())) {
            throw new RoomException(RoomErrorEnum.ROOM_NO_PERMISSION);
        }
        roomGroupHelper.clearApplyAndMember(uid, roomId);
    }

    /**
     * 修改群组信息
     * <p>
     * 默认实现抛出无权限异常，只有群主和管理员可以修改群组信息
     * </p>
     *
     * @param groupModifyDTO 群组修改信息
     * @throws RoomException 当用户无权限时抛出
     */
    public void modifyGroup(GroupModifyDTO groupModifyDTO) {
        throw new RoomException(RoomErrorEnum.ROOM_NO_PERMISSION);
    }

    /**
     * 退出群聊
     *
     * @param roomId 房间ID
     */
    public void outGroup(Long roomId) {
        Long uid = UserHolder.get().getId();
        roomGroupHelper.clearApplyAndMember(uid, roomId);
    }
    /**
     * 授权成员（设置管理员等）
     * <p>
     * 默认实现抛出无权限异常，只有群主可以授权成员
     * </p>
     *
     * @param handleAuthReq 授权请求
     * @throws RoomException 当用户无权限时抛出
     */
    public void authorizeMember(HandleAuthReq handleAuthReq) {
        throw new RoomException(RoomErrorEnum.ROOM_NO_PERMISSION);
    }

    /**
     * 撤回消息
     * <p>
     * 用户可以撤回自己的消息（2分钟内），管理员和群主可以撤回权限低于自己的用户的消息
     * </p>
     *
     * @param uid     操作用户ID
     * @param message 要撤回的消息
     */
    public void recallMessage(Long uid, Message message) {
        if(uid.equals(message.getFromUid())) {
            recallSelfMessage(message);
            return;
        }
        checkRecall(message);
        Map<Long, Integer> memberUidRoleMap = groupMemberDao.getMemberUidRoleMap(message.getRoomId(), List.of(uid, message.getFromUid()));
        Integer selfRole = memberUidRoleMap.get(uid);
        Integer targetRole = memberUidRoleMap.get(message.getFromUid());
        AssertUtil.isTrue(selfRole > targetRole , RoomErrorEnum.ROOM_NO_PERMISSION);
        recallMsgHandler.recall(uid, message);
    }
    /**
     * 撤回自己的消息
     * <p>
     * 管理员和成员都不可撤回自己超过两分钟的消息，群主可以
     * </p>
     *
     * @param message 要撤回的消息
     */
    public void recallSelfMessage(Message message) {
        long between = DateUtil.between(DateUtil.date(message.getCreateTime()), new Date(), DateUnit.MINUTE);
        AssertUtil.isTrue(between < 2, "超过2分钟的消息无法撤回哦");
        recallMsgHandler.recall(message.getFromUid(), message);
    }


    /**
     * 检查消息是否可以撤回
     *
     * @param message 要检查的消息
     */
    protected void checkRecall(Message message) {
        AssertUtil.isNotEmpty(message, "消息有误");
        AssertUtil.notEqual(message.getType(), MessageTypeEnum.RECALL.getType(), "消息无法撤回");
    }

}
