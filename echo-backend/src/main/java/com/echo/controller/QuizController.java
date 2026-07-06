package com.echo.controller;

import com.echo.dto.request.SubmitQuizRequest;
import com.echo.dto.response.QuizQuestionResponse;
import com.echo.dto.response.QuizResultResponse;
import com.echo.entity.QuizCategory;
import com.echo.entity.QuizAttempt;
import com.echo.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 题库控制器
 */
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    /**
     * 获取所有启用的分类
     * GET /api/quiz/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<QuizCategory>> getCategories() {
        return ResponseEntity.ok(quizService.getCategories());
    }

    /**
     * 获取分类下的题目（含选项）
     * GET /api/quiz/{categoryId}/questions
     */
    @GetMapping("/{categoryId}/questions")
    public ResponseEntity<List<QuizQuestionResponse>> getQuestions(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(quizService.getQuestionsByCategory(categoryId));
    }

    /**
     * 提交答题
     * POST /api/quiz/{categoryId}/submit
     */
    @PostMapping("/{categoryId}/submit")
    public ResponseEntity<QuizResultResponse> submitAnswers(
            @PathVariable UUID categoryId,
            @RequestParam UUID userId,
            @RequestBody SubmitQuizRequest req) {
        return ResponseEntity.ok(quizService.submitAnswers(userId, categoryId, req.getAnswers()));
    }

    /**
     * 获取用户的答题历史
     * GET /api/quiz/attempts?userId=xxx
     */
    @GetMapping("/attempts")
    public ResponseEntity<List<QuizAttempt>> getAttempts(@RequestParam UUID userId) {
        return ResponseEntity.ok(quizService.getAttempts(userId));
    }
}
