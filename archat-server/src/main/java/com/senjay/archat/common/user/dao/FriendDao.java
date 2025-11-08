package com.senjay.archat.common.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senjay.archat.common.user.domain.dto.FriendSearchDTO;
import com.senjay.archat.common.user.domain.entity.Friend;
import com.senjay.archat.common.user.domain.entity.User;
import com.senjay.archat.common.user.domain.vo.response.FriendSearchResp;
import com.senjay.archat.common.user.mapper.FriendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class FriendDao extends ServiceImpl<FriendMapper, Friend> {

    public Page<Friend> getBySearch(FriendSearchDTO friendSearchDTO) {
        Integer status = friendSearchDTO.getStatus();
        Integer page = friendSearchDTO.getPage();
        Integer pageSize = friendSearchDTO.getPageSize();
// 分页查询
        Page<Friend> pages = new Page<>(page, pageSize);
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, Friend::getStatus, status);

//        this 就是 friendDAO 对象
        return this.page(pages, wrapper);
    }

    //    双向关系 查询多条记录最好还是使用list封装 然后savebatch批量处理
    public List<Friend> getUserFriend(Long uid, Long fid) {
        return lambdaQuery().eq(Friend::getUserId, uid)
                .eq(Friend::getFriendId, fid)
                .or()
                .eq(Friend::getUserId, fid)
                .eq(Friend::getFriendId, uid)
                .select(Friend::getId)
                .list();
//        .list() 返回的是 实体对象的列表，而不是单字段值的列表。即使你只查了 id，MyBatis-Plus 也会把这个 id 填充到一个 Friend 实例中。
    }

    public Friend selectByUserAndFriendId(Long uid, Long fid) {
        return lambdaQuery().eq(Friend::getUserId, uid)
                .eq(Friend::getFriendId, fid)
                .one();
    }


}