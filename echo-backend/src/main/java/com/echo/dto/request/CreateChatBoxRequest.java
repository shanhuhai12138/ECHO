package com.echo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建聊天框请求
 */
@Data
public class CreateChatBoxRequest {

    @NotBlank(message = "聊天框名称不能为空")
    @Size(max = 100, message = "名称不能超过 100 个字符")
    private String name;

    /** SELF 或 ROLE，不传默认为 SELF */
    @Size(max = 20)
    private String type;

    /** 角色描述（ROLE 类型时使用） */
    private String roleDescription;

    /** 场景标签 */
    @Size(max = 200)
    private String scenario;
}
