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
 * 聊天框（对话场景）
 * 每个 ChatBox 代表一个独立的对话上下文，包含：
 * - 自对话（"跟我自己聊"）
 * - 角色对话（"跟李四（同事）聊"）
 */
@Entity
@Table(name = "chat_boxes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatBox {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    /** 所属用户 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 聊天框名称（如"张三"、"李四-同事"） */
    @Column(nullable = false, length = 100)
    private String name;

    /** 聊天框类型：SELF（自对话）/ ROLE（角色对话） */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ChatBoxType type = ChatBoxType.SELF;

    /** 角色描述（用户自定义，如"我的同事，性格内向"） */
    @Column(columnDefinition = "TEXT")
    private String roleDescription;

    /** 场景标签（如"绩效面谈"、"分手沟通"） */
    @Column(length = 200)
    private String scenario;

    /** 是否启用 */
    @Builder.Default
    private Boolean enabled = true;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 聊天框类型枚举
     */
    public enum ChatBoxType {
        /** 自对话：跟自己的用户画像聊 */
        SELF,
        /** 角色对话：跟预设角色聊（同事/恋人/朋友等） */
        ROLE
    }
}
