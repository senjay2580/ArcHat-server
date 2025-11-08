package com.senjay.archat.common.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 群聊房间表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("room_group")
public class RoomGroup {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 房间id
     */
    @TableField("room_id")
    private Long roomId;

    /**
     * 群名称
     */
    @TableField("name")
    private String name;

    /**
     * 群头像
     */
    @TableField("avatar")
    private String avatar;

    @TableField("group_desc")
    private String groupDesc;
    /**
     * 逻辑删除(0-正常,1-删除)
     *  @TableLogic 注解，MyBatis-Plus 的删除操作就会自动变成“逻辑删除”，
     *  会真正执行 DELETE，而是会执行 UPDATE 将你指定的逻辑删除字段（比如 delete_status）的值改为你定义的“已删除值”。
     */
    @TableField("delete_status")
    @TableLogic(value = "0", delval = "1")
    private Integer deleteStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;


}
