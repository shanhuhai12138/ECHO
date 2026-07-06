package com.echo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 画像更新日志
 * 记录每次画像变更的原因和数据快照
 */
@Entity
@Table(name = "persona_update_log", indexes = {
    @Index(name = "idx_persona_log_user_time", columnList = "user_id, created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaUpdateLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属用户 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 被更新的维度标识 */
    @Column(nullable = false, length = 100)
    private String dimensionKey;

    /** 更新前的数据快照（JSON 字符串） */
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String oldValue;

    /** 更新后的数据快照（JSON 字符串） */
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String newValue;

    /** 触发类型：quiz_result / chat_sentiment / manual */
    @Column(nullable = false, length = 50)
    private String triggerType;

    /** 触发时的原始数据快照（JSON 字符串） */
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String triggerData;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
