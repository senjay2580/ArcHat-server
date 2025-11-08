package com.senjay.archat.common.service;

import com.senjay.archat.common.chat.dao.ContactDao;
import com.senjay.archat.common.chat.dao.GroupMemberDao;
import com.senjay.archat.common.chat.dao.RoomDao;
import com.senjay.archat.common.chat.dao.RoomFriendDao;
import com.senjay.archat.common.chat.domain.entity.GroupMember;
import com.senjay.archat.common.chat.domain.entity.Room;
import com.senjay.archat.common.chat.domain.entity.RoomFriend;
import com.senjay.archat.common.chat.domain.enums.RoomStatusEnum;
import com.senjay.archat.common.chat.domain.enums.RoomTypeEnum;
import com.senjay.archat.common.exception.RoomException;
import com.senjay.archat.common.exception.errorEnums.RoomErrorEnum;
import com.senjay.archat.common.user.dao.FriendDao;
import com.senjay.archat.common.user.domain.entity.Friend;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.senjay.archat.common.constant.RedisConstant.GROUP_MEMBER_COUNT_KEY;

// helpler 类 这个类是为了防止事务传播失效创建的！！
@Service
@RequiredArgsConstructor
public class RoomHelperService {
    private final RoomDao roomDao;
    private final RoomFriendDao roomFriendDao;
    private final ContactDao contactDao;
    private final FriendDao friendDao;
    private final StringRedisTemplate stringRedisTemplate;
    private final GroupMemberDao groupMemberDao;
    @Transactional(rollbackFor = Exception.class)
    // room 创建流
    public void createRoomFlow(Long uid1, Long uid2) {
        Friend friend = friendDao.lambdaQuery().eq(Friend::getUserId, uid1)
                .eq(Friend::getFriendId, uid2)
                .one();
        if(friend == null) {
            throw new RoomException(RoomErrorEnum.ROOM_INVITE_FAILED);
        }
        RoomFriend roomFriend ;
        Room room = Room.builder()
                .type(RoomTypeEnum.FRIEND.getType())
                .activeTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        roomDao.save(room);
        roomFriend = RoomFriend.builder()
                .uid1(uid1)
                .uid2(uid2)
                .roomId(room.getId())
                .roomKey(uid1 + "_" + uid2)
                .status(RoomStatusEnum.AVAILABLE.getCode())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        roomFriendDao.save(roomFriend);
        contactDao.createContact(uid1, uid2, roomFriend.getRoomId());
    }
    public Long getMemberCount(Long roomId) {
        String key = GROUP_MEMBER_COUNT_KEY +  roomId;
        String memberCount = stringRedisTemplate.opsForValue().get(key);
        if(memberCount == null) {
            Long count = groupMemberDao.lambdaQuery()
                    .eq(GroupMember::getGroupId, roomId)
                    .count();
            memberCount = String.valueOf(count);
            stringRedisTemplate.opsForValue().set(key, memberCount, 1, TimeUnit.HOURS);

        }

        return  Long.valueOf(memberCount);
    }
    public void changeMemberCount(Long memberCount, Integer delta, Long roomId)  {
        String key = GROUP_MEMBER_COUNT_KEY +  roomId;
        Long result = memberCount + delta;
        stringRedisTemplate.opsForValue().set(key, result.toString(), 1, TimeUnit.HOURS);

    }

    public void clearUpMember(Long roomId) {
        String key = GROUP_MEMBER_COUNT_KEY +  roomId;
        stringRedisTemplate.delete(key);
    }

}
