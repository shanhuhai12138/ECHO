package com.echo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 对话消息
 * 每条消息属于一个 ChatBox，按时间顺序排列
 */
@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_chat_messages_box_time", columnList = "chat_box_id, created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    /** 所属聊天框 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_box_id", nullable = false)
    private ChatBox chatBox;

    /** 消息方向：USER（用户发的）/ ASSISTANT（AI 回的） */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageType messageType;

    /** 消息内容 */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        /** 用户发送的消息 */
        USER,
        /** AI 返回的消息 */
        ASSISTANT
    }
}
