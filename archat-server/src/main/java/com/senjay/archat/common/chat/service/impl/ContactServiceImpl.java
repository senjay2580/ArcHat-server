package com.senjay.archat.common.chat.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.chat.dao.ContactDao;
import com.senjay.archat.common.chat.domain.vo.request.BasePageReq;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupQueryResp;
import com.senjay.archat.common.chat.domain.vo.response.PrivateContactResp;
import com.senjay.archat.common.chat.domain.vo.response.UnreadMsgCntResp;
import com.senjay.archat.common.chat.mapper.ContactMapper;
import com.senjay.archat.common.chat.service.ContactService;
import com.senjay.archat.common.util.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final ContactDao contactDao;
    private final ContactMapper contactMapper;
    @Override
    public IPage<PrivateContactResp> listPrivate(BasePageReq contactQueryReq) {
        Long uid = UserHolder.get().getId();
        Integer page = contactQueryReq.getPage();
        Integer pageSize = contactQueryReq.getPageSize();
        Page<PrivateContactResp> pages = new Page<>(page, pageSize);
        return contactMapper.listPrivate(pages, uid);

    }

    @Override
    public IPage<GroupQueryResp> listGroup(BasePageReq contactQueryReq) {
        Long uid = UserHolder.get().getId();
        Integer page = contactQueryReq.getPage();
        Integer pageSize = contactQueryReq.getPageSize();
        Page<GroupQueryResp> pages = new Page<>(page, pageSize);
        return contactMapper.listGroup(pages, uid);
    }

    @Override
    public IPage<UnreadMsgCntResp> getUnreadMsgCnt(BasePageReq basePageReq) {
        Long uid = UserHolder.get().getId();
        Integer page = basePageReq.getPage();
        Integer pageSize = basePageReq.getPageSize();
        Page<UnreadMsgCntResp> pages = new Page<>(page, pageSize);
        return contactMapper.getUnreadMsgCnt(pages, uid);
    }
}

