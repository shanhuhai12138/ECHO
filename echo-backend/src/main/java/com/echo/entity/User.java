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
 * 用户实体
 * ECHO 平台的核心用户模型
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    /** 用户名（登录用，唯一） */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 昵称（展示用） */
    @Column(length = 100)
    private String nickname;

    /** 密码（BCrypt 加密存储） */
    @Column(nullable = false)
    private String password;

    /** 是否启用 */
    @Builder.Default
    private Boolean enabled = true;

    /** 注册时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 最后更新时间 */
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
}
