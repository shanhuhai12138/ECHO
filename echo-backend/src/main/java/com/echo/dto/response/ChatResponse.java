package com.echo.dto.response;

import lombok.Data;

import java.util.UUID;

/**
 * 对话响应
 */
@Data
public class ChatResponse {

    private UUID messageId;
    private String content;
    private String personaReply;  // 冲突模式下：画像视角回复
    private String objectiveAdvice; // 冲突模式下：客观建议
    private boolean isConflictMode;
}
