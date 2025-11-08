package com.senjay.archat.common.exception;

import com.senjay.archat.common.exception.errorEnums.ErrorEnum;
import lombok.Getter;

/**
 * 自定义房间异常，用于处理 Room 模块相关业务错误
 */
@Getter
public class RoomException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer errorCode;

    /**
     * 错误信息
     */
    private final String errorMsg;

    public RoomException() {
        super("房间异常");
        this.errorCode = null;
        this.errorMsg = "房间异常";
    }

    public RoomException(String errorMsg) {
        super(errorMsg);
        this.errorCode = null;
        this.errorMsg = errorMsg;
    }

    public RoomException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public RoomException(Integer errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public RoomException(ErrorEnum error) {
        super(error.getErrorMsg());
        this.errorCode = error.getErrorCode();
        this.errorMsg = error.getErrorMsg();
    }

    public RoomException(ErrorEnum error, Throwable cause) {
        super(error.getErrorMsg(), cause);
        this.errorCode = error.getErrorCode();
        this.errorMsg = error.getErrorMsg();
    }

    @Override
    public String toString() {
        return "RoomException{" +
                "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
