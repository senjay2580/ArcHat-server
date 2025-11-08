package com.senjay.archat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.mapper.MessageMapper;
import org.springframework.stereotype.Service;

@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {
}

