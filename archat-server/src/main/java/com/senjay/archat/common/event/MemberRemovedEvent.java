package com.senjay.archat.common.event;

import com.senjay.archat.common.chat.domain.vo.request.UserAndRoomReq;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MemberRemovedEvent extends ApplicationEvent {
    private UserAndRoomReq userAndRoomReq;
    public MemberRemovedEvent(Object source, UserAndRoomReq userAndRoomReq) {
        super(source);
        this.userAndRoomReq = userAndRoomReq;
    }
}
