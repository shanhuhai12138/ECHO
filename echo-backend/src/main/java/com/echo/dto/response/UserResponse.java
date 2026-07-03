package com.echo.dto.response;

import lombok.Data;

import java.util.UUID;

/**
 * 注册/登录成功后的用户信息响应
 */
@Data
public class UserResponse {

    private UUID id;
    private String username;
    private String nickname;

    public static UserResponse from(com.echo.entity.User user) {
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setNickname(user.getNickname());
        return resp;
    }
}
