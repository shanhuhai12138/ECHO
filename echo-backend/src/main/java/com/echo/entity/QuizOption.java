package com.echo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

/**
 * 题库选项
 * 每个选项映射到一个或多个画像维度
 */
@Entity
@Table(name = "quiz_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizOption {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    /** 所属题目 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    /** 选项文本 */
    @Column(nullable = false, length = 500)
    private String text;

    /** 映射的画像维度（JSON 格式） */
    @Column(columnDefinition = "jsonb")
    private String scoreMapping;

    /** 排序 */
    @Builder.Default
    private Integer sortOrder = 0;
}
