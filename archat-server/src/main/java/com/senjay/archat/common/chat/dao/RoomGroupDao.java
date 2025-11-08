package com.senjay.archat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senjay.archat.common.chat.domain.entity.RoomGroup;
import com.senjay.archat.common.chat.mapper.RoomGroupMapper;
import org.springframework.stereotype.Service;

//MyBatis-Plus 逻辑删除插件会自动加 delete_status = 0，只查未删除的。
//如果有数据返回就是存在，直接返回 true，否则返回 false。
//这样写完全避免了 null 引用问题，也不用再手动判断 deleteStatus。
@Service
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {
    public boolean checkGroupIsExist(Long roomId) {
        return lambdaQuery()
                .eq(RoomGroup::getRoomId, roomId)
                .exists();
    }

}
