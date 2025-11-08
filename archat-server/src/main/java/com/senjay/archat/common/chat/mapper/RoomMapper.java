package com.senjay.archat.common.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.chat.domain.entity.Room;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupApplyResp;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupDetailResp;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupMemberSimpleResp;
import com.senjay.archat.common.chat.domain.vo.response.group.MyApplyResp;
import org.apache.ibatis.annotations.Param;

public interface RoomMapper extends BaseMapper<Room> {
    IPage<GroupMemberSimpleResp> getGroupMember(Page<?> pages, @Param("roomId") Long roomId);

    IPage<GroupApplyResp> listJoinApply(Page<?> pages, @Param("uid") Long uid);

    IPage<GroupDetailResp> searchGroup(Page<?> pages, @Param("keyword") String keyword );

    IPage<MyApplyResp> listMyApply(Page<?> pages, @Param("uid") Long uid);
}
