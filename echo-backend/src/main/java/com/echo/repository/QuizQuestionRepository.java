package com.echo.repository;

import com.echo.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, UUID> {

    /** 获取分类下的所有启用的题目（按排序） */
    List<QuizQuestion> findByCategoryIdAndActiveTrueOrderBySortOrderAsc(UUID categoryId);
}
