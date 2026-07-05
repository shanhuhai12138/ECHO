package com.echo.util;

import com.echo.entity.ChatBox;
import com.echo.entity.PersonaDimension;
import com.echo.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Prompt 组装器
 * 根据用户画像和聊天框信息，动态组装 system prompt
 */
@Component
public class PromptBuilder {

    /**
     * 组装对话的 system prompt
     * 包含：用户画像摘要 + 聊天框场景 + 对话原则
     */
    public String buildSystemPrompt(User user, ChatBox chatBox, List<PersonaDimension> dimensions) {
        StringBuilder sb = new StringBuilder();

        // 1. 基础指令
        sb.append("你是 ECHO，一个情绪辅助与生活指导 AI 助手。\n\n");

        // 2. 用户画像
        sb.append("## 用户信息\n");
        sb.append("用户名：").append(user.getUsername()).append("\n");
        if (user.getNickname() != null) {
            sb.append("昵称：").append(user.getNickname()).append("\n");
        }
        sb.append("\n");

        // 3. 画像维度摘要
        if (dimensions != null && !dimensions.isEmpty()) {
            sb.append("## 用户画像\n");
            for (PersonaDimension dim : dimensions) {
                if (dim.getScore() != null && dim.getScore().compareTo(java.math.BigDecimal.ZERO) > 0) {
                    sb.append(String.format("- %s: %.0f/100", dim.getDimensionLabel(), dim.getScore()));
                    if (dim.getTextSummary() != null) {
                        sb.append(" (").append(dim.getTextSummary()).append(")");
                    }
                    sb.append("\n");
                }
            }
            sb.append("\n");
        }

        // 4. 聊天框场景
        sb.append("## 当前对话场景\n");
        sb.append("对话名称：").append(chatBox.getName()).append("\n");
        sb.append("类型：").append(chatBox.getType()).append("\n");

        if (chatBox.getScenario() != null) {
            sb.append("场景：").append(chatBox.getScenario()).append("\n");
        }
        if (chatBox.getRoleDescription() != null) {
            sb.append("角色描述：").append(chatBox.getRoleDescription()).append("\n");
        }
        sb.append("\n");

        // 5. 对话原则
        sb.append("## 对话原则\n");
        sb.append("1. 用温暖、理解、不评判的语气回应\n");
        sb.append("2. 结合用户画像给出个性化的建议\n");
        sb.append("3. 不要过度迎合用户，也不要刻意否定用户\n");
        sb.append("4. 如果检测到消极情绪，先给予关怀，再帮助解决问题\n");
        sb.append("5. 回答简洁有力，不要长篇大论\n");
        sb.append("6. 使用中文回答\n");

        return sb.toString();
    }

    /**
     * 组装用户消息（用于发送给 LLM）
     */
    public String buildUserMessage(String content) {
        return content;
    }
}
