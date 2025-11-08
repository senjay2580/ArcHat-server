package com.senjay.archat.common.exception;

import com.senjay.archat.common.exception.errorEnums.CommonErrorEnum;
import com.senjay.archat.common.user.domain.vo.response.Result;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e) {
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> 
            errorMsg.append(fieldError.getField())
                   .append(fieldError.getDefaultMessage())
                   .append(", ")
        );
        
        String message = errorMsg.toString();
        if (message.endsWith(", ")) {
            message = message.substring(0, message.length() - 2);
        }
        
        log.warn("参数校验失败: {}", message);
        return Result.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(), message);
    }
    /**
     * 文件上传大小超限异常处理
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("文件上传大小超限: {}", e.getMessage());
        return Result.fail("文件大小不能超过1MB");
    }

    /**
     * JWT过期异常处理
     */
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result handleExpiredJwtException(ExpiredJwtException e) {
        log.warn("JWT令牌已过期: {}", e.getMessage());
        return Result.fail("登录已过期，请重新登录");
    }

    /**
     * 系统异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result handleSystemException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.fail(CommonErrorEnum.SYSTEM_ERROR);
    }
    /**
     * 用户业务异常处理
     */
    @ExceptionHandler(UserException.class)
    public Result handleUserException(UserException e) {
        log.warn("用户操作异常: {}", e.getMessage());
        if (e.getErrorCode() != null) {
            return Result.fail(e.getErrorCode(), e.getMessage());
        }
        return Result.fail(e.getMessage());
    }

    /**
     * 房间业务异常处理
     */
    @ExceptionHandler(RoomException.class)
    public Result handleRoomException(RoomException e) {
        log.warn("房间操作异常: {}", e.getMessage());
        if (e.getErrorCode() != null) {
            return Result.fail(e.getErrorCode(), e.getMessage());
        }
        return Result.fail(e.getMessage());
    }

    /**
     * 通用业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getErrorCode(), e.getMessage());
    }
    /**
     * 数据库约束异常处理
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result handleSQLIntegrityConstraintViolation(SQLIntegrityConstraintViolationException e) {
        String message = e.getMessage();
        log.warn("数据库约束异常: {}", message);
        
        if (message != null && message.contains("Duplicate entry")) {
            String duplicateValue = extractDuplicateValue(message);
            return Result.fail(409, "数据重复，冲突键值：" + duplicateValue);
        }
        
        return Result.fail(500, "数据库约束异常：" + message);
    }

    /**
     * 提取重复键值信息
     */
    private String extractDuplicateValue(String message) {
        try {
            return message.split("Duplicate entry '")[1].split("' for key")[0];
        } catch (Exception e) {
            log.debug("解析重复键值失败: {}", e.getMessage());
            return "未知";
        }
    }
}
