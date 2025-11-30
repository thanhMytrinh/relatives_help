package com.relativesHelp.relativesHelp.user.controller;

import com.relativesHelp.relativesHelp.common.dto.ApiResponse;
import com.relativesHelp.relativesHelp.security.dto.ClientMetadata;
import com.relativesHelp.relativesHelp.user.dto.AuthResponse;
import com.relativesHelp.relativesHelp.user.dto.LoginRequest;
import com.relativesHelp.relativesHelp.user.dto.RefreshTokenRequest;
import com.relativesHelp.relativesHelp.user.dto.RegisterRequest;
import com.relativesHelp.relativesHelp.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request,
                                                              HttpServletRequest httpRequest) {
        AuthResponse response = authService.register(request, buildMetadata(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
                                                           HttpServletRequest httpRequest) {
        AuthResponse response = authService.login(request, buildMetadata(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request,
                                                                  HttpServletRequest httpRequest) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken(), buildMetadata(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    private ClientMetadata buildMetadata(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String deviceInfo = request.getHeader("X-Device-Info");
        if (!StringUtils.hasText(deviceInfo)) {
            deviceInfo = userAgent;
        }

        return ClientMetadata.builder()
                .ipAddress(resolveClientIp(request))
                .userAgent(userAgent)
                .deviceInfo(deviceInfo)
                .build();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

