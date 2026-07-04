package com.echo.controller;

import com.echo.dto.request.PersonaDimensionRequest;
import com.echo.dto.response.PersonaDimensionResponse;
import com.echo.dto.response.PersonaSummaryResponse;
import com.echo.entity.PersonaUpdateLog;
import com.echo.service.PersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 画像管理控制器
 */
@RestController
@RequestMapping("/api/persona")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    /**
     * 获取用户完整画像
     * GET /api/persona/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<PersonaSummaryResponse> getProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(personaService.getFullProfile(userId));
    }

    /**
     * 获取用户所有画像维度列表
     * GET /api/persona/{userId}/dimensions
     */
    @GetMapping("/{userId}/dimensions")
    public ResponseEntity<List<PersonaDimensionResponse>> getDimensions(@PathVariable UUID userId) {
        return ResponseEntity.ok(personaService.getDimensions(userId));
    }

    /**
     * 更新或创建画像维度（幂等）
     * PUT /api/persona/{userId}/dimensions
     */
    @PutMapping("/{userId}/dimensions")
    public ResponseEntity<PersonaDimensionResponse> upsertDimension(
            @PathVariable UUID userId,
            @Valid @RequestBody PersonaDimensionRequest req) {
        return ResponseEntity.ok(personaService.upsertDimension(userId, req));
    }

    /**
     * 初始化画像（批量写入初始维度）
     * POST /api/persona/{userId}/initialize
     */
    @PostMapping("/{userId}/initialize")
    public ResponseEntity<List<PersonaDimensionResponse>> initializeProfile(
            @PathVariable UUID userId,
            @RequestBody List<PersonaDimensionRequest> dims) {
        return ResponseEntity.ok(personaService.initializeProfile(userId, dims));
    }

    /**
     * 删除一个画像维度
     * DELETE /api/persona/{userId}/dimensions/{key}
     */
    @DeleteMapping("/{userId}/dimensions/{dimensionKey}")
    public ResponseEntity<Void> removeDimension(
            @PathVariable UUID userId,
            @PathVariable String dimensionKey) {
        personaService.removeDimension(userId, dimensionKey);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取画像变更历史
     * GET /api/persona/{userId}/history
     */
    @GetMapping("/{userId}/history")
    public ResponseEntity<List<PersonaUpdateLog>> getHistory(@PathVariable UUID userId) {
        return ResponseEntity.ok(personaService.getUpdateHistory(userId));
    }
}
