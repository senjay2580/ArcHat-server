package com.senjay.archat.common.chat.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senjay.archat.common.chat.domain.dto.RoomSearchDTO;
import com.senjay.archat.common.chat.domain.entity.Room;
import com.senjay.archat.common.chat.domain.entity.RoomFriend;
import com.senjay.archat.common.chat.domain.enums.RoomStatusEnum;
import com.senjay.archat.common.chat.mapper.RoomFriendMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomFriendDao extends ServiceImpl<RoomFriendMapper, RoomFriend> {



    public void removeRoom(Integer id) {
        LambdaQueryWrapper<RoomFriend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomFriend::getRoomId, id);
        remove(wrapper);

    }


    public Page<RoomFriend> queryPage(RoomSearchDTO roomSearchDTO) {
        Long uid1 = roomSearchDTO.getUid1();
        Long uid2 = roomSearchDTO.getUid2();
        Integer status = roomSearchDTO.getStatus();
        if (uid1 != null && uid2 != null) {
            long min = Math.min(uid1, uid2);
            long max = Math.max(uid1, uid2);
            uid1 = min;
            uid2 = max;
        }
        Integer page = roomSearchDTO.getPage();
        Integer pageSize = roomSearchDTO.getPageSize();
        Page<RoomFriend> pages = new Page<>(page, pageSize);
        LambdaQueryWrapper<RoomFriend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(uid1 != null, RoomFriend::getUid1, uid1)
                .eq(uid2 != null, RoomFriend::getUid2, uid2)
                .eq(status != null, RoomFriend::getStatus, status);

        return this.page(pages,wrapper);

    }

    public void convertStatus(Long roomId, Integer status) {
        lambdaUpdate().eq(RoomFriend::getRoomId,roomId)
                .set(RoomFriend::getStatus,status)
                .update();
        return ;

    }

    public void deleteBatch(List<Long> ids) {
        LambdaQueryWrapper<RoomFriend> wp = new LambdaQueryWrapper<>();
        wp.in(RoomFriend::getRoomId,ids);
        remove(wp);
    }
}
