package com.echo.repository;

import com.echo.entity.QuizCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizCategoryRepository extends JpaRepository<QuizCategory, UUID> {

    /** 获取所有启用的分类（按排序） */
    List<QuizCategory> findByActiveTrueOrderBySortOrderAsc();
}
