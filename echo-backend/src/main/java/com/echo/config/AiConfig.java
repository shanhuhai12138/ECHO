package com.echo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * AI 模型配置
 * 通过 application.yml 注入 API Key 和 Base URL
 * 初期支持通义千问，后续可扩展其他模型
 */
@Configuration
@Data
public class AiConfig {

    /** 模型名称 */
    @Value("${echo.ai.model:qwen-max}")
    private String model;

    /** API Base URL（通义千问） */
    @Value("${echo.ai.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    /** API Key */
    @Value("${echo.ai.api-key:}")
    private String apiKey;

    /** 最大 token 数 */
    @Value("${echo.ai.max-tokens:2048}")
    private int maxTokens;

    /** 温度（创造性） */
    @Value("${echo.ai.temperature:0.7}")
    private double temperature;

    /** 超时时间（毫秒） */
    @Value("${echo.ai.timeout:30000}")
    private int timeout;
}
