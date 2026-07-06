package com.echo.service;

import com.echo.dto.response.QuizQuestionResponse;
import com.echo.dto.response.QuizResultResponse;
import com.echo.entity.*;
import com.echo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 题库服务
 * 管理题目查询、答题提交、画像映射
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizCategoryRepository categoryRepo;
    private final QuizQuestionRepository questionRepo;
    private final QuizOptionRepository optionRepo;
    private final QuizAttemptRepository attemptRepo;
    private final UserRepository userRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取所有启用的分类
     */
    @Transactional(readOnly = true)
    public List<QuizCategory> getCategories() {
        return categoryRepo.findByActiveTrueOrderBySortOrderAsc();
    }

    /**
     * 获取分类下的所有题目（含选项）
     */
    @Transactional(readOnly = true)
    public List<QuizQuestionResponse> getQuestionsByCategory(UUID categoryId) {
        List<QuizQuestion> questions = questionRepo.findByCategoryIdAndActiveTrueOrderBySortOrderAsc(categoryId);
        QuizCategory category = categoryRepo.findById(categoryId).orElse(null);

        return questions.stream().map(q -> {
            QuizQuestionResponse resp = new QuizQuestionResponse();
            resp.setId(q.getId());
            resp.setQuestionText(q.getQuestionText());
            resp.setQuestionType(q.getQuestionType());
            resp.setCategory(category != null ? category.getName() : "");

            List<QuizOption> options = optionRepo.findByQuestionIdOrderBySortOrderAsc(q.getId());
            resp.setOptions(options.stream().map(opt -> {
                QuizQuestionResponse.QuizOptionResponse oResp = new QuizQuestionResponse.QuizOptionResponse();
                oResp.setId(opt.getId());
                oResp.setText(opt.getText());
                oResp.setSortOrder(opt.getSortOrder());
                return oResp;
            }).toArray(QuizQuestionResponse.QuizOptionResponse[]::new));

            return resp;
        }).toList();
    }

    /**
     * 提交答题并更新画像
     */
    @Transactional
    public QuizResultResponse submitAnswers(UUID userId, UUID categoryId, Map<String, Integer> answers) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        QuizCategory category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        // 1. 获取题目和选项
        List<QuizQuestion> questions = questionRepo.findByCategoryIdAndActiveTrueOrderBySortOrderAsc(categoryId);
        if (questions.isEmpty()) {
            throw new RuntimeException("该分类下没有题目");
        }

        // 2. 计算总分 + 画像影响
        BigDecimal totalScore = BigDecimal.ZERO;
        Map<String, Object> profileDelta = new LinkedHashMap<>();

        for (QuizQuestion q : questions) {
            String qId = q.getId().toString();
            Integer answerIndex = answers.get(qId);
            if (answerIndex == null) continue;

            List<QuizOption> options = optionRepo.findByQuestionIdOrderBySortOrderAsc(q.getId());
            if (answerIndex >= 0 && answerIndex < options.size()) {
                QuizOption selected = options.get(answerIndex);

                // 累计分数（每题满分 10 分）
                totalScore = totalScore.add(BigDecimal.TEN);

                // 解析选项映射的画像维度
                try {
                    if (selected.getScoreMapping() != null && !selected.getScoreMapping().isBlank()) {
                        Map<String, Object> mapping = objectMapper.readValue(
                                selected.getScoreMapping(), Map.class);
                        String key = (String) mapping.get("dimensionKey");
                        if (key != null) {
                            profileDelta.merge(key, mapping, (existing, newVal) -> {
                                // 合并多个选项对同一维度的影响
                                @SuppressWarnings("unchecked")
                                Map<String, Object> merged = (Map<String, Object>) existing;
                                @SuppressWarnings("unchecked")
                                Map<String, Object> newMap = (Map<String, Object>) newVal;
                                merged.putAll(newMap);
                                return merged;
                            });
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse score mapping for option {}: {}", selected.getId(), e.getMessage());
                }
            }
        }

        // 3. 保存答题记录
        String answersJson;
        String deltaJson;
        try {
            answersJson = objectMapper.writeValueAsString(answers);
            deltaJson = objectMapper.writeValueAsString(profileDelta);
        } catch (Exception e) {
            log.error("Failed to serialize quiz data", e);
            answersJson = "{}";
            deltaJson = "{}";
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .user(user)
                .category(category)
                .answers(answersJson)
                .totalScore(totalScore)
                .profileDelta(deltaJson)
                .build();
        attemptRepo.save(attempt);

        // 4. 更新用户画像
        updatePersonasFromQuiz(userId, profileDelta);

        // 5. 构建响应
        QuizResultResponse result = new QuizResultResponse();
        result.setCategoryId(categoryId);
        result.setCategoryName(category.getName());
        result.setTotalScore(totalScore);
        result.setProfileDelta(deltaJson);
        result.setCompletedAt(attempt.getCreatedAt());
        return result;
    }

    /**
     * 将答题结果映射到画像维度
     */
    private void updatePersonasFromQuiz(UUID userId, Map<String, Object> profileDelta) {
        // 这里调用 PersonaService 的 upsertDimension
        // 由于避免循环依赖，直接操作 repository
        // 后续可以抽成独立的服务
        log.info("Quiz profile delta for user {}: {}", userId, profileDelta);
        // TODO: 调用 PersonaService.upsertDimension 更新每个维度
    }

    /**
     * 获取用户的答题历史
     */
    @Transactional(readOnly = true)
    public List<QuizAttempt> getAttempts(UUID userId) {
        return attemptRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
