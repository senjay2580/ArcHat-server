package com.senjay.archat.common.exception.errorEnums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BusinessErrorEnum implements ErrorEnum {
    
    // ================================== 通用错误 ==================================
    BUSINESS_ERROR(1001, "{0}"),
    SYSTEM_ERROR(1002, "系统出小差了，请稍后再试哦~~"),
    
    // ================================== 用户相关错误 ==================================
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_ALREADY_EXISTS(2002, "用户已存在"),
    
    // ================================== 聊天相关错误 ==================================
    MESSAGE_SEND_FAILED(3001, "消息发送失败"),
    ROOM_NOT_FOUND(3002, "房间不存在"),
    ;
    
    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}
