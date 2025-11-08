package com.senjay.archat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senjay.archat.common.chat.domain.entity.Contact;
import com.senjay.archat.common.chat.mapper.ContactMapper;
import com.senjay.archat.common.util.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {
    // 事务传播？（不同方法）
    @Transactional
    public void createContact(Long uid1, Long uid2, Long roomId) {
        Contact contact4U1 = buildContact(uid1, roomId);
        Contact contact4U2 = buildContact(uid2, roomId);
        // 批量插入
        saveBatch(List.of(contact4U1, contact4U2));
    }
    public Contact buildContact(Long uid, Long roomId) {
        LocalDateTime now = LocalDateTime.now();
        Contact contact = new Contact();
        contact.setUid(uid);
        contact.setRoomId(roomId);
        contact.setReadTime(now);
        contact.setCreateTime(now);
        contact.setUpdateTime(now);
        return contact;
    }

    public void refreshOrCreateActiveTime(Long roomId, List<Long> memberUidList, Long msgId, LocalDateTime activeTime) {
        baseMapper.refreshOrCreateActiveTime(roomId, memberUidList, msgId, activeTime);
    }

    public void updateReadTime(Long roomId, Long uid) {
        lambdaUpdate().eq(Contact::getRoomId, roomId)
                .eq(Contact::getUid, uid)
                .set(Contact::getReadTime, LocalDateTime.now())
                .update();
    }
}
