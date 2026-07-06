package com.echo.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 画像更新日志响应
 */
@Data
public class UpdateLogResponse {

    private Long id;
    private String dimensionKey;
    private String oldValue;
    private String newValue;
    private String triggerType;
    private LocalDateTime createdAt;

    public static UpdateLogResponse from(com.echo.entity.PersonaUpdateLog log) {
        UpdateLogResponse resp = new UpdateLogResponse();
        resp.setId(log.getId());
        resp.setDimensionKey(log.getDimensionKey());
        resp.setOldValue(log.getOldValue());
        resp.setNewValue(log.getNewValue());
        resp.setTriggerType(log.getTriggerType());
        resp.setCreatedAt(log.getCreatedAt());
        return resp;
    }
}
