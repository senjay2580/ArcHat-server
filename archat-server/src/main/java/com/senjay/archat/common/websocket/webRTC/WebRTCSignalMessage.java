package com.senjay.archat.common.websocket.webRTC;

import lombok.Data;

/**
 * @author 33813
 */
@Data
public class WebRTCSignalMessage {
    private String type;
    private String callId;
    private Long targetUserId;
    // offer 信令数据
    private Object offer;
    // answer 信令数据
    private Object answer;
    // ice-candidate 信令数据
    private Object candidate;
    private CallerInfo callerInfo;
    // === 新增：通话恢复相关字段 ===
    private Long currentUserId;        // 当前用户ID
    private Boolean success;           // 操作成功标志
    private Object restoreInfo;        // 恢复信息
    private Object reconnectInfo;      // 重连信息

    // === 新增：错误处理字段 ===
    private String error;              // 错误信息
    private String reason;             // 拒绝/失败原因

    // === 新增：扩展信息字段 ===
    private Long fromUserId;           // 发送方用户ID（用于信令路由）
    private Long timestamp;            // 时间戳
    private Object metadata;           // 扩展元数据
}
