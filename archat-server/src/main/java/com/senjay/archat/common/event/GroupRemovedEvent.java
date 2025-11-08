package com.senjay.archat.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GroupRemovedEvent extends ApplicationEvent {
    private Long roomId;

    public GroupRemovedEvent(Object source, Long roomId) {
        super(source);
        this.roomId = roomId;
    }
}
