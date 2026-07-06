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
 * 用户答题记录
 * 保存用户的答题结果和对画像的影响
 */
@Entity
@Table(name = "quiz_attempts", indexes = {
    @Index(name = "idx_quiz_attempt_user", columnList = "user_id, created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttempt {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    /** 所属用户 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 所属分类 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private QuizCategory category;

    /** 答题结果（JSON：{"questionId": "answerIndex"}） */
    @Column(columnDefinition = "jsonb", nullable = false)
    private String answers;

    /** 总分 */
    @Column(precision = 5, scale = 2)
    private BigDecimal totalScore;

    /** 对画像的影响（JSON：{"dimensionKey": {"score": 75, "summary": "..."}}） */
    @Column(columnDefinition = "jsonb")
    private String profileDelta;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
