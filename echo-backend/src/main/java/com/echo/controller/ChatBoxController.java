package com.echo.controller;

import com.echo.dto.request.CreateChatBoxRequest;
import com.echo.dto.response.ChatBoxResponse;
import com.echo.entity.ChatBox;
import com.echo.entity.User;
import com.echo.repository.ChatBoxRepository;
import com.echo.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 聊天框控制器
 * 管理用户的对话场景（自对话 / 角色对话）
 */
@RestController
@RequestMapping("/api/chatboxes")
@RequiredArgsConstructor
public class ChatBoxController {

    private final ChatBoxRepository chatBoxRepository;
    private final UserRepository userRepository;

    /**
     * 获取用户的所有聊天框
     * GET /api/chatboxes?userId=xxx
     */
    @GetMapping
    public ResponseEntity<List<ChatBoxResponse>> listBoxes(@RequestParam UUID userId) {
        List<ChatBox> boxes = chatBoxRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<ChatBoxResponse> responses = boxes.stream()
                .map(ChatBoxResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * 创建新的聊天框
     * POST /api/chatboxes/{userId}
     */
    @PostMapping("/{userId}")
    public ResponseEntity<ChatBoxResponse> createBox(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateChatBoxRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        ChatBox box = ChatBox.builder()
                .user(user)
                .name(req.getName())
                .type(req.getType() != null
                        ? ChatBox.ChatBoxType.valueOf(req.getType())
                        : ChatBox.ChatBoxType.SELF)
                .roleDescription(req.getRoleDescription())
                .scenario(req.getScenario())
                .enabled(true)
                .build();

        box = chatBoxRepository.save(box);
        return ResponseEntity.ok(ChatBoxResponse.from(box));
    }
}
