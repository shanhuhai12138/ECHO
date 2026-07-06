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
 * 题库题目
 */
@Entity
@Table(name = "quiz_questions", indexes = {
    @Index(name = "idx_quiz_q_category", columnList = "category_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestion {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    /** 所属分类 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private QuizCategory category;

    /** 题目文本 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    /** 题目类型：single(单选) / likert(量表) */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String questionType = "single";

    /** 排序 */
    @Builder.Default
    private Integer sortOrder = 0;

    /** 是否启用 */
    @Builder.Default
    private Boolean active = true;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
