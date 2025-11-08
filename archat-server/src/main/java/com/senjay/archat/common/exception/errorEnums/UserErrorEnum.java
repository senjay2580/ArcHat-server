package com.senjay.archat.common.exception.errorEnums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum UserErrorEnum implements ErrorEnum{
    USERNAME_EXIST(-1,"用户名已存在 !"),
    ACCESS_ERROR(-2,"用户名或者密码不正确! "),

    ;
    private final Integer code;
    private final String msg;
    @Override
    public Integer getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.msg;
    }
}
