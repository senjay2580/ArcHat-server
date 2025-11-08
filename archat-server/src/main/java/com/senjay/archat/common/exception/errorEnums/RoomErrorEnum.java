package com.senjay.archat.common.exception.errorEnums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomErrorEnum implements ErrorEnum{

    ROOM_NOT_FOUND(4001, "房间不存在"),
    ROOM_ALREADY_EXISTS(4002, "房间已存在"),
    ROOM_CREATE_FAILED(4003, "创建房间失败"),
    ROOM_DELETE_FAILED(4004, "删除房间失败"),
    ROOM_UPDATE_FAILED(4005, "更新房间信息失败"),
    ROOM_MEMBER_LIMIT_REACHED(4006, "房间成员数量已达上限"),
    ROOM_NOT_JOINED(4007, "用户未加入该房间"),
    ROOM_ALREADY_JOINED(4008, "用户已在房间中"),
    ROOM_NO_PERMISSION(4009, "无权限操作该房间"),
    ROOM_TYPE_INVALID(4010, "无效的房间类型"),
    ROOM_OWNER_CANNOT_QUIT(4011, "房主不能退出房间"),
    ROOM_MEMBER_NOT_FOUND(4012, "房间成员不存在"),
    ROOM_TRANSFER_FAILED(4013, "转让房主失败"),
    ROOM_NAME_DUPLICATED(4014, "房间名重复"),
    ROOM_MUTED(4015, "房间已被禁言"),
    ROOM_CLOSED(4016, "房间已关闭"),
    ROOM_PASSWORD_INCORRECT(4017, "房间密码错误"),
    ROOM_INVITE_FAILED(4018, "邀请成员失败");
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
