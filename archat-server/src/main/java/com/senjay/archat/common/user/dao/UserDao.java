package com.senjay.archat.common.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senjay.archat.common.user.domain.dto.UserSearchDTO;
import com.senjay.archat.common.user.domain.entity.User;
import com.senjay.archat.common.user.domain.vo.response.FriendSearchResp;
import com.senjay.archat.common.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDao extends ServiceImpl<UserMapper, User> {
    public Page<User> getBySearch(UserSearchDTO userSearchDTO) {
        String keyword = userSearchDTO.getKeyword();
        Integer exepMin = userSearchDTO.getExepMin();
        Integer exepMax = userSearchDTO.getExepMax();
        Integer page = userSearchDTO.getPage();
        Integer pageSize = userSearchDTO.getPageSize();

        Page<User> pages = new Page<>(page,pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(keyword != null,User::getUsername, keyword)
                .ge(exepMin != null,User::getExep,exepMin)
                .le(exepMax != null,User::getExep,exepMax);
        return this.page(pages,wrapper);


    }
// 使用userMapper.xml 避免转换映射
//    public Page<FriendSearchResp> search(String keyword, Integer page, Integer pageSize) {
//        Page<FriendSearchResp> pages = new Page<>(page,pageSize);
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        wrapper.like(User::getUsername, keyword)
//                .select(User::getUsername, User::getAvatar, User::getExep, User::getCreateTime, User::getStatus)
//                .orderByDesc(User::getCreateTime);
//        return this.page(pages,wrapper);
//    }
////    在这里可以使用userMapper的方法也可以使用 Iservice接口的方法
//    baseMapper.gerById()





}
