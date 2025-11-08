package com.senjay.archat.common.chat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.chat.domain.dto.GroupApplyDTO;
import com.senjay.archat.common.chat.domain.dto.GroupAuthDTO;
import com.senjay.archat.common.chat.domain.dto.GroupModifyDTO;
import com.senjay.archat.common.chat.domain.dto.RoomSearchDTO;
import com.senjay.archat.common.chat.domain.entity.RoomFriend;
import com.senjay.archat.common.chat.domain.vo.request.*;
import com.senjay.archat.common.chat.domain.vo.response.PrivateContactResp;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupApplyResp;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupDetailResp;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupMemberSimpleResp;
import com.senjay.archat.common.chat.domain.vo.response.group.MyApplyResp;

import java.util.List;

public interface RoomService {
    void createPrivateRoom(AdminCreatePrivateRoomReq createPrivateRoomReq);



    void deletePrivateRoom(Integer id);

    Page<RoomFriend> listRoom(RoomSearchDTO roomSearchDTO);

    void convertStatus(StatusConvertReq statusConvertReq);

    void delBatchOnPrivate(List<Long> ids);

   void addPrivateRoom(ClientCreatePrivateRoomReq clientCreatePrivateRoomReq);

    Boolean checkPrivateRoom(Long fid);


    void addGroupRoom(CreateGroupRoomReq createGroupRoomReq);

    void joinGroup(GroupAuthDTO joinGroupDTO);

    GroupDetailResp getGroupDetail(Long roomId) ;

    void dismissGroup(Long roomId);

    void applyJoinGroup(GroupApplyDTO groupApplyDTO);

    IPage<GroupApplyResp> listJoinApply(BasePageReq basePageReq);

    IPage<MyApplyResp> listMyApply(BasePageReq basePageReq);

    void applyDealWith(HandleApplyReq handleApplyReq);



    void outGroup(Long roomId);

    void removeMember(UserAndRoomReq removeMemberReq);

    IPage<GroupMemberSimpleResp> listGroupMember(Long roomId, BasePageReq basePageReq);

    Long countMember(Long roomId);

    IPage<GroupDetailResp> searchGroup(BasePageReq basePageReq, String keyword);


    void modifyApplyMsg(ModifyApplyMsgReq modifyApplyMsgReqs);

    void inviteToBeMember(UserAndRoomReq userAndRoomReq);

    void modifyGroup(GroupModifyDTO groupModifyDTO);
    void authorizeMember(HandleAuthReq handleAuthReq);
}
