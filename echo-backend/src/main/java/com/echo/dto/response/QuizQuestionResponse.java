package com.echo.dto.response;

import lombok.Data;

import java.util.UUID;

/**
 * 题目响应
 */
@Data
public class QuizQuestionResponse {

    private UUID id;
    private String questionText;
    private String questionType;
    private String category;
    private QuizOptionResponse[] options;

    @Data
    public static class QuizOptionResponse {
        private UUID id;
        private String text;
        private int sortOrder;
    }
}
