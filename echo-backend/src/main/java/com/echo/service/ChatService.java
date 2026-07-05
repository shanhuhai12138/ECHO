package com.echo.service;

import com.echo.dto.request.ChatRequest;
import com.echo.dto.response.ChatResponse;
import com.echo.entity.ChatBox;
import com.echo.entity.ChatMessage;
import com.echo.entity.PersonaDimension;
import com.echo.entity.User;
import com.echo.repository.ChatBoxRepository;
import com.echo.repository.ChatMessageRepository;
import com.echo.repository.PersonaDimensionRepository;
import com.echo.repository.UserRepository;
import com.echo.util.PromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 对话服务
 * 核心业务逻辑：保存消息 + 调用 AI + 返回回复
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatBoxRepository chatBoxRepo;
    private final ChatMessageRepository messageRepo;
    private final UserRepository userRepo;
    private final PersonaDimensionRepository personaRepo;
    private final AiService aiService;
    private final PromptBuilder promptBuilder;

    /**
     * 发送消息并获取 AI 回复
     */
    @Transactional
    public ChatResponse sendMessage(UUID chatBoxId, String content) {
        // 1. 获取聊天框
        ChatBox chatBox = chatBoxRepo.findById(chatBoxId)
                .orElseThrow(() -> new RuntimeException("聊天框不存在"));

        // 2. 保存用户消息
        ChatMessage userMessage = ChatMessage.builder()
                .chatBox(chatBox)
                .messageType(ChatMessage.MessageType.USER)
                .content(content)
                .build();
        messageRepo.save(userMessage);

        // 3. 获取用户画像
        User user = chatBox.getUser();
        List<PersonaDimension> dimensions = personaRepo.findByUserIdOrderByUpdatedAtDesc(user.getId());

        // 4. 组装 system prompt
        String systemPrompt = promptBuilder.buildSystemPrompt(user, chatBox, dimensions);

        // 5. 调用 AI
        String aiReply = aiService.chat(systemPrompt, content);

        // 6. 保存 AI 回复
        ChatMessage assistantMessage = ChatMessage.builder()
                .chatBox(chatBox)
                .messageType(ChatMessage.MessageType.ASSISTANT)
                .content(aiReply)
                .build();
        messageRepo.save(assistantMessage);

        // 7. 构建响应
        ChatResponse response = new ChatResponse();
        response.setMessageId(assistantMessage.getId());
        response.setContent(aiReply);
        response.setConflictMode(false);
        return response;
    }

    /**
     * 获取聊天框的消息历史
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(UUID chatBoxId) {
        return messageRepo.findByChatBoxIdOrderByCreatedAtAsc(chatBoxId);
    }
}
