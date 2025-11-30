package com.relativesHelp.relativesHelp.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    /**
     * Access token dùng cho Authorization header (trước đây là field "token").
     */
    private String token;

    /**
     * Refresh token dùng để lấy access token mới.
     */
    private String refreshToken;

    private String type = "Bearer";
    private UserDto user;
}

