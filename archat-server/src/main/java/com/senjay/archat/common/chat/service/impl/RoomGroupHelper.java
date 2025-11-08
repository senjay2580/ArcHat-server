package com.senjay.archat.common.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.senjay.archat.common.chat.dao.GroupApplyDao;
import com.senjay.archat.common.chat.domain.entity.GroupApply;
import com.senjay.archat.common.chat.domain.vo.request.UserAndRoomReq;
import com.senjay.archat.common.event.MemberRemovedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomGroupHelper {
    @Autowired
    private GroupApplyDao groupApplyDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Transactional(rollbackFor = Exception.class)
    public void clearApplyAndMember(Long uid, Long roomId) {
        groupApplyDao.remove(new LambdaQueryWrapper<GroupApply>()
                .eq(GroupApply::getRoomId, roomId)
                .eq(GroupApply::getUid, uid)
        );
        applicationEventPublisher.publishEvent(new MemberRemovedEvent(this,new UserAndRoomReq(uid,roomId)));
    }
}
