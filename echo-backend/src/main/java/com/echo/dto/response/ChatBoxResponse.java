package com.echo.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 聊天框响应
 */
@Data
public class ChatBoxResponse {

    private UUID id;
    private String name;
    private String type;       // SELF / ROLE
    private String roleDescription;
    private String scenario;
    private LocalDateTime createdAt;

    public static ChatBoxResponse from(com.echo.entity.ChatBox box) {
        ChatBoxResponse resp = new ChatBoxResponse();
        resp.setId(box.getId());
        resp.setName(box.getName());
        resp.setType(box.getType().name());
        resp.setRoleDescription(box.getRoleDescription());
        resp.setScenario(box.getScenario());
        resp.setCreatedAt(box.getCreatedAt());
        return resp;
    }
}
