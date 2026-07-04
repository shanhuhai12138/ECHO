package com.echo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户画像维度
 * 每个维度代表用户的一个特征属性（如大五人格、沟通风格等）
 */
@Entity
@Table(name = "persona_dimensions", indexes = {
    @Index(name = "idx_persona_dim_user_key", columnList = "user_id, dimension_key", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaDimension {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    /** 所属用户 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 维度标识（如 big_five_openness, communication_style） */
    @Column(nullable = false, length = 100)
    private String dimensionKey;

    /** 维度中文名（如"开放性"、"沟通风格"） */
    @Column(nullable = false, length = 200)
    private String dimensionLabel;

    /** 量化分数 0-100 */
    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    /** AI 生成的自然语言描述 */
    @Column(columnDefinition = "TEXT")
    private String textSummary;

    /** 置信度 0-1 */
    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal confidence = BigDecimal.ZERO;

    /** 数据来源：initial_qa（初始问答）/ quiz（题库测评）/ chat_analysis（对话分析）/ manual（手动） */
    @Column(nullable = false, length = 50)
    @Builder.Default
    private String source = "initial_qa";

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
}
