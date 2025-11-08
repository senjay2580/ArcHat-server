package com.senjay.archat.common.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.user.domain.dto.FriendApplyQueryDTO;
import com.senjay.archat.common.user.domain.dto.FriendDTO;
import com.senjay.archat.common.user.domain.dto.FriendSearchDTO;
import com.senjay.archat.common.user.domain.entity.Friend;
import com.senjay.archat.common.user.domain.vo.request.FriendApplyDealWithReq;
import com.senjay.archat.common.user.domain.vo.request.FriendApplyReq;
import com.senjay.archat.common.user.domain.vo.request.FriendSearchReq;
import com.senjay.archat.common.user.domain.vo.response.FriendApplyResp;
import com.senjay.archat.common.user.domain.vo.response.FriendSearchResp;

import java.util.List;

public interface FriendService {
    String addFriend(FriendApplyReq friendApplyReq);

    String approveOrNot(FriendApplyDealWithReq friendApplyDealWithReq);

    String deleteFriend(Long friendId);

    String createAndAdd(FriendDTO friendDTO);

    String deleteById(Long id);

    Page<Friend> listFriend(FriendSearchDTO friendSearchDTO);

    IPage<FriendSearchResp> search(FriendSearchReq friendSearchReq);

    List<FriendSearchResp> listFriends(Long uid);

    List<Long> getFriendIds(Long uid);
    IPage<FriendApplyResp> listMyApply(FriendApplyQueryDTO friendApplyQueryDTO);

    IPage<FriendApplyResp> listMyReceive(FriendApplyQueryDTO friendApplyQueryDTO);

    Boolean checkFriendship(Long fid);
}
