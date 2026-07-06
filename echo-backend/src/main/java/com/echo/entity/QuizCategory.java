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
 * 题库分类
 * 如"大五人格简版"、"沟通风格测评"
 */
@Entity
@Table(name = "quiz_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizCategory {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    /** 分类名称 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 分类描述 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 排序 */
    @Builder.Default
    private Integer sortOrder = 0;

    /** 是否启用 */
    @Builder.Default
    private Boolean active = true;

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
