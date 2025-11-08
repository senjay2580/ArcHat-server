package com.senjay.archat.common.user.domain.vo.response.ws;

import com.senjay.archat.common.chat.domain.vo.response.ChatMemberResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSOnlineOfflineNotify {

    //新的上下线用户
    private List<ChatMemberResp> changeList = new ArrayList<>();

    //在线人数
    private Long onlineNum;
}
