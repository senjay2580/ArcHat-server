package com.senjay.archat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senjay.archat.common.chat.domain.entity.Room;
import com.senjay.archat.common.chat.domain.entity.RoomFriend;
import com.senjay.archat.common.chat.mapper.RoomMapper;
import org.springframework.stereotype.Service;

@Service
public class RoomDao extends ServiceImpl<RoomMapper, Room> {

}
