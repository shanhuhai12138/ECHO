package com.echo.dto.request;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 提交答题请求
 */
@Data
public class SubmitQuizRequest {

    /** 题目 ID → 答案索引 */
    private Map<String, Integer> answers;
}
