package com.senjay.archat.common.user.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "friends")
@EqualsAndHashCode(callSuper = false)
@Builder
public class Friend {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("friend_id")
    private Long friendId;
    @TableField("status")
    private Integer status;
    @TableField("create_time")
    private LocalDateTime createTime;

}
