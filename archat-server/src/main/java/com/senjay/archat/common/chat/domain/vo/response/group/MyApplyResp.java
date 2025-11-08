package com.senjay.archat.common.chat.domain.vo.response.group;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 33813
 */
@Data
public class MyApplyResp {
//    申请记录唯一id
    private Long id;
    private Long roomId;
//      群聊名字
    private String name;
//    我对这个群聊房间的申请信息
    private String msg;
    private Integer status;
    private String avatar;
//    申请记录最新时间 允许修改msg
    private LocalDateTime updateTime;
}
