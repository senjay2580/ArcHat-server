package com.senjay.archat.common.exception.errorEnums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorEnum implements ErrorEnum{
    SYSTEM_ERROR(-1, "系统出小差了，请稍后再试哦~~"),
    PARAM_VALID(-2, "参数校验失败{0}"),
    FREQUENCY_LIMIT(-3, "请求太频繁了，请稍后再试哦~~"),
    LOCK_LIMIT(-4, "请求太频繁了，请稍后再试哦~~"),
    ;
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
    // 面向接口编程 用于以下的功能
//
//    public UserException(ErrorEnum error) {
//        super(error.getErrorMsg());
//        this.errorCode = error.getErrorCode();
//        this.errorMsg = error.getErrorMsg();
//    }
}
