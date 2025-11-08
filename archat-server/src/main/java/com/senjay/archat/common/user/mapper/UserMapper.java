package com.senjay.archat.common.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.user.domain.entity.User;
import com.senjay.archat.common.user.domain.vo.response.FriendSearchResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserMapper extends BaseMapper<User> {

    // 如果分页，使用 IPage 参数，MyBatis-Plus 自动分页
    IPage<FriendSearchResp> searchFriend(Page<?> friendSearchRespPage, @Param("keyword") String keyword);

    List<FriendSearchResp> listFriends(@Param("friendIds") List<Long> friendIds);
}
