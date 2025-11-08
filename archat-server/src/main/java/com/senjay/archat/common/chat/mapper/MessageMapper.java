package com.senjay.archat.common.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senjay.archat.common.chat.domain.entity.Message;
import com.senjay.archat.common.chat.domain.vo.request.MessageQueryReq;
import com.senjay.archat.common.chat.domain.vo.response.MessageResp;
import com.senjay.archat.common.chat.domain.vo.response.group.ChatMessageResp;
import dev.langchain4j.agent.tool.P;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {
    List<MessageResp> listMessage(MessageQueryReq messageQueryReq);


}
