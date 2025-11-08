package com.senjay.archat.common.chat.domain.vo.response.ws;

import lombok.Data;

@Data
public class WSBaseResp<T> {
    /**
     * ws推送给前端的消息
     *
     */
//    请求和返回的数据都需要有类型
    private Integer type;
    private T data;
}
