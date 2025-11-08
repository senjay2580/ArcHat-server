package com.senjay.archat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FriendTipsEnum {
    NOT_EXIST_FRIEND("没有好友关系！"),
    REPEAT_CLICK_ERROR("请不要重复操作!"),
    HAD_FRIEND("已经存在好友关系！"),
    SEND_APPLY("发送申请！"),
    ADD_SUCCESS("添加好友成功！"),
    INSERT_FRIENDSHIP("插入好友关系记录！"),
    REJECT_APPLY("你拒绝了该申请！"),
    DELETE_SUCCESS("删除好友成功！"),
    REMOVE_SUCCESS("删除记录成功！"),
    APPLY_ONSELF_ERROR("不能添加自己对自己的好友关系！")
    ;
    private final String desc;


}
