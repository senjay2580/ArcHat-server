package com.senjay.archat.common.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.senjay.archat.common.chat.domain.enums.RoomTypeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * <p>
 * 房间表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("room")
@Builder  // 实体类不建议使用builder
//不需要构造函数
public class Room {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("type")
    private Integer type;

    @TableField("active_time")
    private LocalDateTime activeTime;

    @TableField("last_msg_id")
    private Long lastMsgId;


    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;


    @JsonIgnore // 控制对象序列化为 JSON 时忽略某些字段或方法的输出
    public boolean isRoomFriend() {
        return RoomTypeEnum.of(this.type) == RoomTypeEnum.FRIEND;
    }

    @JsonIgnore
    public boolean isRoomGroup() {
        return RoomTypeEnum.of(this.type) == RoomTypeEnum.GROUP;
    }
}
