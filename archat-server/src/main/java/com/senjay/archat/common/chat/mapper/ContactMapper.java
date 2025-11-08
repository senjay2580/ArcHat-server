package com.senjay.archat.common.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.chat.domain.entity.Contact;
import com.senjay.archat.common.chat.domain.vo.response.group.GroupQueryResp;
import com.senjay.archat.common.chat.domain.vo.response.PrivateContactResp;
import com.senjay.archat.common.chat.domain.vo.response.UnreadMsgCntResp;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface ContactMapper extends BaseMapper<Contact> {


//    @Param 导入的包要正确
    IPage<PrivateContactResp> listPrivate(Page<?> pages, @Param("uid") Long uid);

    IPage<GroupQueryResp> listGroup(Page<?> pages, @Param("uid") Long uid);

    IPage<UnreadMsgCntResp> getUnreadMsgCnt(Page<?> pages, @Param("uid") Long uid);

    void refreshOrCreateActiveTime(Long roomId, List<Long> memberUidList, Long msgId, LocalDateTime activeTime);
}
