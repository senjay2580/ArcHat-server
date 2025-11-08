package com.senjay.archat.common.user.domain.vo.response;

import com.senjay.archat.common.exception.errorEnums.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    // 自定义逻辑状态码
    private int code;
    private String msg;
    // 返回体
    private T data;



    public static <T> Result<T> success(T data) {
        return new Result<T>(200,"执行成功",data);
    }
    public static Result success() {
        return new Result(200,"执行成功",null);
    }



    // 查表 阿里java开发手册1
    public static <T> Result<T> fail(int code,String msg){
        return new Result<T>(code,msg,null);
    }
    // 500 不明确错误
    public static  Result fail(String msg){
        return new Result(500,msg,null);
    }
    public static <T> Result<T> fail(ErrorEnum errorEnum){
        return new Result<T>(errorEnum.getErrorCode(),errorEnum.getErrorMsg(),null);
    }


}
