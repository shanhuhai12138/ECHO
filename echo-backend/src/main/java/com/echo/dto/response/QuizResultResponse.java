package com.echo.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 答题结果响应
 */
@Data
public class QuizResultResponse {

    private UUID categoryId;
    private String categoryName;
    private BigDecimal totalScore;
    private String profileDelta;
    private LocalDateTime completedAt;
}
