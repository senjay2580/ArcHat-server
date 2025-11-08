package com.senjay.archat.common.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.user.domain.entity.Friend;
import com.senjay.archat.common.user.domain.vo.response.FriendApplyResp;
import com.senjay.archat.common.user.domain.vo.response.FriendSearchResp;
import org.apache.ibatis.annotations.Param;

public interface FriendMapper extends BaseMapper<Friend> {

    IPage<FriendApplyResp> listApply(Page<?> pages, @Param("uid") Long uid);

    IPage<FriendApplyResp> listReceive(Page<?> pages, @Param("uid") Long uid);
}
