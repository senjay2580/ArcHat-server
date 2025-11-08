package com.senjay.archat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senjay.archat.common.chat.domain.entity.GroupApply;
import com.senjay.archat.common.chat.mapper.GroupApplyMapper;
import org.springframework.stereotype.Service;

@Service
public class GroupApplyDao extends ServiceImpl<GroupApplyMapper, GroupApply> {
}
