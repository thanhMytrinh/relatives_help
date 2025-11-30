package com.relativesHelp.relativesHelp.user.service;

import com.relativesHelp.relativesHelp.entity.User;
import com.relativesHelp.relativesHelp.entity.UserSession;
import com.relativesHelp.relativesHelp.entity.UserToken;
import com.relativesHelp.relativesHelp.kafka.event.UserRegisteredEvent;
import com.relativesHelp.relativesHelp.kafka.producer.KafkaEventProducer;
import com.relativesHelp.relativesHelp.repository.UserRepository;
import com.relativesHelp.relativesHelp.security.JwtTokenProvider;
import com.relativesHelp.relativesHelp.security.dto.ClientMetadata;
import com.relativesHelp.relativesHelp.security.service.TokenStoreService;
import com.relativesHelp.relativesHelp.security.service.UserSessionService;
import com.relativesHelp.relativesHelp.user.dto.AuthResponse;
import com.relativesHelp.relativesHelp.user.dto.LoginRequest;
import com.relativesHelp.relativesHelp.user.dto.RegisterRequest;
import com.relativesHelp.relativesHelp.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final KafkaEventProducer kafkaEventProducer;
    private final UserSessionService userSessionService;
    private final TokenStoreService tokenStoreService;

    @Transactional
    public AuthResponse register(RegisterRequest request, ClientMetadata metadata) {
        UserDto userDto = userService.register(request);
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        publishRegisteredEvent(user);
        return issueTokensForNewSession(user, metadata);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, ClientMetadata metadata) {
        User user = userRepository.findByEmail(request.getEmailOrUsername())
                .orElseGet(() -> userRepository.findByPhone(request.getEmailOrUsername())
                        .orElseThrow(() -> new RuntimeException("Invalid credentials")));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new RuntimeException("User account is not active");
        }

        userService.updateLastLogin(user.getId());
        return issueTokensForNewSession(user, metadata);
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken, ClientMetadata metadata) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new RuntimeException("Refresh token is required");
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token type");
        }

        Long userId = jwtTokenProvider.extractUserId(refreshToken);
        String sessionId = jwtTokenProvider.extractSessionId(refreshToken);

        if (!StringUtils.hasText(sessionId)) {
            throw new RuntimeException("Invalid session identifier");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new RuntimeException("User account is not active");
        }

        UserSession session = userSessionService.findValidSession(sessionId)
                .filter(s -> s.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Session is invalid or expired"));

        UserToken storedToken = tokenStoreService.findValidRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token has been revoked or expired"));

        tokenStoreService.revokeToken(storedToken);
        userSessionService.extendSession(session, jwtTokenProvider.getRefreshTokenValidityMillis());

        return issueTokens(user, session, metadata);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return;
        }

        tokenStoreService.findRefreshToken(refreshToken)
                .ifPresent(token -> {
                    if (token.getSession() != null) {
                        tokenStoreService.revokeSessionTokens(token.getSession());
                        userSessionService.deactivateSession(token.getSession());
                    } else {
                        tokenStoreService.revokeToken(token);
                    }
                });
    }

    private AuthResponse issueTokensForNewSession(User user, ClientMetadata metadata) {
        UserSession session = userSessionService.createSession(
                user,
                jwtTokenProvider.getRefreshTokenValidityMillis(),
                metadata
        );
        return issueTokens(user, session, metadata);
    }

    private AuthResponse issueTokens(User user, UserSession session, ClientMetadata metadata) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId(), session.getSessionId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getId(), session.getSessionId());

        tokenStoreService.storeRefreshToken(
                user,
                session,
                refreshToken,
                jwtTokenProvider.getRefreshTokenValidityMillis(),
                metadata
        );

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(UserDto.fromEntity(user))
                .build();
    }

    private void publishRegisteredEvent(User user) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .registeredAt(LocalDateTime.now())
                .build();
        kafkaEventProducer.publishUserRegistered(event);
    }
}

