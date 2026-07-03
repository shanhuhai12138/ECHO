package com.echo.repository;

import com.echo.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 对话消息数据访问层
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /** 查询某个聊天框的所有消息（按时间正序） */
    List<ChatMessage> findByChatBoxIdOrderByCreatedAtAsc(UUID chatBoxId);

    /** 查询最近 N 条消息（用于对话上下文） */
    List<ChatMessage> findTop20ByChatBoxIdOrderByCreatedAtDesc(UUID chatBoxId);
}
