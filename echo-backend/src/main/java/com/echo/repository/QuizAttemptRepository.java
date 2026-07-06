package com.echo.repository;

import com.echo.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {

    /** 获取用户的所有答题记录（按时间倒序） */
    List<QuizAttempt> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /** 获取用户在某分类下的答题记录 */
    List<QuizAttempt> findByUserIdAndCategoryIdOrderByCreatedAtDesc(UUID userId, UUID categoryId);
}
