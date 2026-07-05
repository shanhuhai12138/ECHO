package com.echo.service;

import com.echo.config.AiConfig;
import com.echo.util.PromptBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * AI 服务
 * 封装与 LLM API 的交互
 * 初期支持通义千问，后续可扩展其他模型
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final AiConfig aiConfig;
    private final PromptBuilder promptBuilder;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送对话请求，获取 AI 回复
     */
    public String chat(String systemPrompt, String userMessage) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = Map.of(
                "model", aiConfig.getModel(),
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userMessage)
                ),
                "temperature", aiConfig.getTemperature(),
                "max_tokens", aiConfig.getMaxTokens()
            );

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getApiKey());
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            log.debug("Calling LLM API: model={}, message={}", aiConfig.getModel(), userMessage);
            ResponseEntity<String> response = restTemplate.exchange(
                aiConfig.getBaseUrl() + "/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
            );

            // 解析响应
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }

            log.warn("LLM API returned empty response");
            return "抱歉，我暂时无法回复。请稍后再试。";

        } catch (Exception e) {
            log.error("LLM API call failed: {}", e.getMessage());
            // 降级：返回友好提示
            return "抱歉，AI 服务暂时不可用，请稍后再试。";
        }
    }

    /**
     * 检查 API Key 是否配置
     */
    public boolean isConfigured() {
        return aiConfig.getApiKey() != null && !aiConfig.getApiKey().isBlank();
    }
}
