package com.echo.service;

import com.echo.dto.request.PersonaDimensionRequest;
import com.echo.dto.response.PersonaDimensionResponse;
import com.echo.dto.response.PersonaSummaryResponse;
import com.echo.entity.PersonaDimension;
import com.echo.entity.PersonaUpdateLog;
import com.echo.entity.User;
import com.echo.repository.PersonaDimensionRepository;
import com.echo.repository.PersonaUpdateLogRepository;
import com.echo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 用户画像服务
 * 管理画像维度的增删改查和变更日志
 */
@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaDimensionRepository dimensionRepo;
    private final PersonaUpdateLogRepository logRepo;
    private final UserRepository userRepo;

    /**
     * 获取用户完整画像
     */
    @Transactional(readOnly = true)
    public PersonaSummaryResponse getFullProfile(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        List<PersonaDimension> dims = dimensionRepo.findByUserIdOrderByUpdatedAtDesc(userId);
        List<PersonaDimensionResponse> responses = dims.stream()
                .map(PersonaDimensionResponse::from)
                .toList();

        return PersonaSummaryResponse.from(user.getUsername(), user.getNickname(), responses);
    }

    /**
     * 获取用户所有画像维度列表
     */
    @Transactional(readOnly = true)
    public List<PersonaDimensionResponse> getDimensions(UUID userId) {
        return dimensionRepo.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(PersonaDimensionResponse::from)
                .toList();
    }

    /**
     * 更新或创建画像维度（幂等操作）
     * 如果维度已存在则更新，否则创建新维度
     */
    @Transactional
    public PersonaDimensionResponse upsertDimension(UUID userId, PersonaDimensionRequest req) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 查找是否已存在
        PersonaDimension existing = dimensionRepo.findByUserIdAndDimensionKey(userId, req.getDimensionKey()).orElse(null);

        // 记录旧值（用于日志）
        String oldJson = existing != null ? toJson(existing) : null;

        PersonaDimension dim;
        if (existing != null) {
            // 更新已有维度
            dim = existing;
            dim.setScore(req.getScore());
            dim.setTextSummary(req.getTextSummary());
            dim.setConfidence(req.getConfidence());
            dim.setSource(req.getSource() != null ? req.getSource() : dim.getSource());
        } else {
            // 创建新维度
            dim = PersonaDimension.builder()
                    .user(user)
                    .dimensionKey(req.getDimensionKey())
                    .dimensionLabel(req.getDimensionLabel())
                    .score(req.getScore())
                    .textSummary(req.getTextSummary())
                    .confidence(req.getConfidence())
                    .source(req.getSource() != null ? req.getSource() : "initial_qa")
                    .build();
        }

        dim = dimensionRepo.save(dim);

        // 记录变更日志
        recordLog(userId, req.getDimensionKey(), oldJson, toJson(dim), "manual", null);

        return PersonaDimensionResponse.from(dim);
    }

    /**
     * 删除一个画像维度
     */
    @Transactional
    public void removeDimension(UUID userId, String dimensionKey) {
        PersonaDimension dim = dimensionRepo.findByUserIdAndDimensionKey(userId, dimensionKey)
                .orElseThrow(() -> new RuntimeException("维度不存在"));
        dimensionRepo.delete(dim);
    }

    /**
     * 初始化画像（批量写入初始维度）
     */
    @Transactional
    public List<PersonaDimensionResponse> initializeProfile(UUID userId, List<PersonaDimensionRequest> dims) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return dims.stream()
                .map(req -> {
                    PersonaDimension dim = PersonaDimension.builder()
                            .user(user)
                            .dimensionKey(req.getDimensionKey())
                            .dimensionLabel(req.getDimensionLabel())
                            .score(req.getScore())
                            .textSummary(req.getTextSummary())
                            .confidence(req.getConfidence())
                            .source("initial_qa")
                            .build();
                    dim = dimensionRepo.save(dim);
                    return PersonaDimensionResponse.from(dim);
                })
                .toList();
    }

    /**
     * 获取用户画像变更历史
     */
    @Transactional(readOnly = true)
    public List<PersonaUpdateLog> getUpdateHistory(UUID userId) {
        return logRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ========== 私有辅助方法 ==========

    private String toJson(PersonaDimension dim) {
        return """
            {"key":"%s","label":"%s","score":%s,"summary":"%s"}
            """.formatted(
            dim.getDimensionKey(),
            dim.getDimensionLabel(),
            dim.getScore(),
            dim.getTextSummary() != null ? dim.getTextSummary().replace("\"", "\\\"") : ""
        );
    }

    private void recordLog(UUID userId, String dimensionKey, String oldVal, String newVal, String triggerType, String triggerData) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return;

        PersonaUpdateLog log = PersonaUpdateLog.builder()
                .user(user)
                .dimensionKey(dimensionKey)
                .oldValue(oldVal)
                .newValue(newVal)
                .triggerType(triggerType)
                .triggerData(triggerData)
                .build();
        logRepo.save(log);
    }
}
