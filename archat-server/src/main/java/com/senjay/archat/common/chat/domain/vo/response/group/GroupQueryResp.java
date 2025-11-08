package com.senjay.archat.common.chat.domain.vo.response.group;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor //  如果用 @Builder，最好加上这个

//当你用 @Builder + @Data Lombok 会为你生成一个 Builder 构造器 和一个 全参数构造方法，
// 但 某些 MyBatis 映射方式（尤其是使用构造方法映射） 会错误推断字段类型或参数顺序。
public class GroupQueryResp {
    // 会话id
    private Long id;
    private Long roomId;
    private String name;
    private String avatar;
    private LocalDateTime activeTime;
    private Long lastMsgId;
}
