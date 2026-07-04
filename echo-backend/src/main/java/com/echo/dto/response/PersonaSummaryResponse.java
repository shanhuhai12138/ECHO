package com.echo.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 完整画像响应
 */
@Data
public class PersonaSummaryResponse {

    private String username;
    private String nickname;
    private List<PersonaDimensionResponse> dimensions;

    public static PersonaSummaryResponse from(String username, String nickname, List<PersonaDimensionResponse> dims) {
        PersonaSummaryResponse resp = new PersonaSummaryResponse();
        resp.setUsername(username);
        resp.setNickname(nickname);
        resp.setDimensions(dims);
        return resp;
    }
}
