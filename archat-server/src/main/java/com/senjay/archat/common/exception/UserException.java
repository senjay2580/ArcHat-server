package com.senjay.archat.common.exception;

import com.senjay.archat.common.exception.errorEnums.ErrorEnum;
import lombok.Data;

@Data
public class UserException extends RuntimeException {
    /**
     *  错误码
     */
    protected Integer errorCode;

    /**
     *  错误信息
     */
    protected String errorMsg;

    public UserException() {
        super();
    }

    public UserException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public UserException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public UserException(Integer errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public UserException(ErrorEnum error) {
        super(error.getErrorMsg());
        this.errorCode = error.getErrorCode();
        this.errorMsg = error.getErrorMsg();
    }

    @Override
    public String getMessage() {
        return errorMsg;
    }

}
