package com.echo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * 发送消息请求
 */
@Data
public class ChatRequest {

    /** 聊天框 ID */
    @NotNull(message = "聊天框 ID 不能为空")
    private UUID chatBoxId;

    /** 用户消息内容 */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /** 是否开启冲突处理双通道模式 */
    @Data
    public static class ConflictResponse {
        private String personaReply;    // 画像视角回复
        private String objectiveAdvice; // 客观建议
    }
}
