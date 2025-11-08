package com.senjay.archat.common.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomMessageDTO {
    private List<Long> uids;
    private Long roomId;
}
