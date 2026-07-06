package com.echo.repository;

import com.echo.entity.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizOptionRepository extends JpaRepository<QuizOption, UUID> {

    /** 获取题目的所有选项（按排序） */
    List<QuizOption> findByQuestionIdOrderBySortOrderAsc(UUID questionId);
}
