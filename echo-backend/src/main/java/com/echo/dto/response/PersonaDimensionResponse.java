package com.echo.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 画像维度响应
 */
@Data
public class PersonaDimensionResponse {

    private UUID id;
    private String dimensionKey;
    private String dimensionLabel;
    private BigDecimal score;
    private String textSummary;
    private BigDecimal confidence;
    private String source;
    private LocalDateTime updatedAt;

    public static PersonaDimensionResponse from(com.echo.entity.PersonaDimension dim) {
        PersonaDimensionResponse resp = new PersonaDimensionResponse();
        resp.setId(dim.getId());
        resp.setDimensionKey(dim.getDimensionKey());
        resp.setDimensionLabel(dim.getDimensionLabel());
        resp.setScore(dim.getScore());
        resp.setTextSummary(dim.getTextSummary());
        resp.setConfidence(dim.getConfidence());
        resp.setSource(dim.getSource());
        resp.setUpdatedAt(dim.getUpdatedAt());
        return resp;
    }
}
