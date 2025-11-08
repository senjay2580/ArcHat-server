package com.senjay.archat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senjay.archat.common.chat.domain.entity.GroupMember;
import com.senjay.archat.common.chat.mapper.GroupMemberMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {

    public GroupMember getMember(Long groupId, Long uid) {
        return lambdaQuery().eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid).one();
    }

    public List<Long> getAllMemberUid(Long roomId) {
        return lambdaQuery()
                //  只查 uid 字段
                .select(GroupMember::getUid)
                .eq(GroupMember::getGroupId,roomId)
                .list()
                .stream()
                //  提取 uid 值
                .map(GroupMember::getUid)
                .toList();

    }

    public Map<Long, Integer> getMemberUidRoleMap(Long groupId, List<Long> uids) {
        return lambdaQuery()
                .select(GroupMember::getUid, GroupMember::getRole)
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uids)
                .list()
                .stream()
                .collect(Collectors.toMap(GroupMember::getUid, GroupMember::getRole));
    }


}
