package com.senjay.archat.common.chat.domain.vo.request;

import lombok.Data;

@Data
public class WSBaseReq {
    /**
     * 请求类型 1.请求登录，2心跳检测
     *
     */
    private Integer type;

    /**
     * 每个请求包具体的数据，类型不同结果不同
     */
    private String data;
}
