package com.senjay.archat.common.chat.strategy;

import com.senjay.archat.common.chat.dao.GroupMemberDao;
import com.senjay.archat.common.chat.domain.dto.GroupModifyDTO;
import com.senjay.archat.common.chat.domain.entity.GroupMember;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.enums.RoleEnum;
import com.senjay.archat.common.chat.domain.vo.request.HandleAuthReq;
import com.senjay.archat.common.chat.strategy.Abstract.AbstractGroupOperator;
import com.senjay.archat.common.exception.RoomException;
import com.senjay.archat.common.exception.errorEnums.RoomErrorEnum;
import com.senjay.archat.common.user.dao.UserDao;
import com.senjay.archat.common.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 群组成员操作器
 * <p>
 * 实现普通成员在群组中的操作权限，包括邀请成员、退出群聊、撤回消息等。
 * 普通成员权限有限，不能移除其他成员、修改群信息等。
 * </p>
 *
 * @author senjay
 * @since 1.0.0
 */
@Service
@Slf4j
public class MemberOperator extends AbstractGroupOperator {
    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private UserDao userDao;
    /**
     * 获取角色代码
     *
     * @return 成员角色代码
     */
    @Override
    public Integer getRole() {
        return RoleEnum.MEMBER.getCode();
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
        String inviterName = userDao.getById(member.getUid()).getUsername();
        String inviteeName = userDao.getById(fid).getUsername();
        log.info("成员[{}]邀请用户[{}]加入群聊[{}]", inviterName, inviteeName, roomId);
    }

    /**
     * 解散群聊
     * <p>
     * 继承父类方法，普通成员无权解散群聊
     * </p>
     *
     * @param roomId 房间ID
     */
    @Override
    public void removeGroup(Long roomId) {
        super.removeGroup(roomId);
    }

    /**
     * 移除群成员
     * <p>
     * 普通成员无权移除其他成员
     * </p>
     *
     * @param roomId 房间ID
     * @param uid    要移除的用户ID
     */
    @Override
    public void removeMember(Long roomId, Long uid) {
        throw new RoomException(RoomErrorEnum.ROOM_NO_PERMISSION);
    }
    /**
     * 修改群组信息
     * <p>
     * 继承父类方法，普通成员无权修改群组信息
     * </p>
     *
     * @param groupModifyDTO 群组修改信息
     */
    @Override
    public void modifyGroup(GroupModifyDTO groupModifyDTO) {
        super.modifyGroup(groupModifyDTO);
    }

    /**
     * 退出群聊
     *
     * @param roomId 房间ID
     */
    @Override
    public void outGroup(Long roomId) {
        super.outGroup(roomId);
    }

    /**
     * 授权成员
     * <p>
     * 继承父类方法，普通成员无权授权其他成员
     * </p>
     *
     * @param handleAuthReq 授权请求
     */
    @Override
    public void authorizeMember(HandleAuthReq handleAuthReq) {
        super.authorizeMember(handleAuthReq);
    }

    /**
     * 撤回消息
     * <p>
     * 普通成员只能撤回自己的消息，不能撤回其他人的消息
     * </p>
     *
     * @param uid     操作用户ID
     * @param message 要撤回的消息
     */
    @Override
    public void recallMessage(Long uid, Message message) {
        checkRecall(message);
        if(uid.equals(message.getFromUid())) {
            // 调用父类的
            recallSelfMessage(message);
            return;
        }
        throw new RoomException(RoomErrorEnum.ROOM_NO_PERMISSION);
    }
}
