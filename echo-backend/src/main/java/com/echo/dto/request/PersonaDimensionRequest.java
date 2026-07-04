package com.echo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建/更新画像维度请求
 */
@Data
public class PersonaDimensionRequest {

    @NotBlank(message = "维度标识不能为空")
    private String dimensionKey;

    @NotBlank(message = "维度名称不能为空")
    private String dimensionLabel;

    /** 分数 0-100 */
    @Min(0)
    @Max(100)
    private BigDecimal score;

    /** AI 生成的描述 */
    private String textSummary;

    /** 置信度 0-1 */
    @Min(0)
    @Max(1)
    private BigDecimal confidence;

    /** 数据来源 */
    private String source;
}
