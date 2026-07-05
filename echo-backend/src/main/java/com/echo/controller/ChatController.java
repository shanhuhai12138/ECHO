package com.echo.controller;

import com.echo.dto.request.ChatRequest;
import com.echo.dto.response.ChatResponse;
import com.echo.entity.ChatMessage;
import com.echo.service.AiService;
import com.echo.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 对话控制器
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final AiService aiService;

    /**
     * 发送消息
     * POST /api/chat/{chatBoxId}
     */
    @PostMapping("/{chatBoxId}")
    public ResponseEntity<ChatResponse> sendMessage(
            @PathVariable UUID chatBoxId,
            @Valid @RequestBody ChatRequest req) {
        return ResponseEntity.ok(chatService.sendMessage(chatBoxId, req.getContent()));
    }

    /**
     * 获取聊天框的消息历史
     * GET /api/chat/{chatBoxId}/messages
     */
    @GetMapping("/{chatBoxId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable UUID chatBoxId) {
        return ResponseEntity.ok(chatService.getMessages(chatBoxId));
    }

    /**
     * 健康检查（AI 服务状态）
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "configured", aiService.isConfigured(),
            "status", "ok"
        ));
    }
}
