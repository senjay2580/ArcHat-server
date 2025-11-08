package com.senjay.archat.common.chat.domain.vo.request.msg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 消息撤回
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MsgRecall implements Serializable {
    private static final long serialVersionUID = 1L;
    //撤回消息的uid 可能是自己 有可能是群主或者管理员
    private Long recallUid;
    //撤回的时间点
    private Date recallTime;
}
