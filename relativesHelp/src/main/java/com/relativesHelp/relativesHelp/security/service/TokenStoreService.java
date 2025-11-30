package com.relativesHelp.relativesHelp.security.service;

import com.relativesHelp.relativesHelp.entity.User;
import com.relativesHelp.relativesHelp.entity.UserSession;
import com.relativesHelp.relativesHelp.entity.UserToken;
import com.relativesHelp.relativesHelp.repository.UserTokenRepository;
import com.relativesHelp.relativesHelp.security.dto.ClientMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenStoreService {
    private final UserTokenRepository userTokenRepository;

    @Transactional
    public void storeRefreshToken(User user,
                                  UserSession session,
                                  String refreshToken,
                                  long refreshValidityMillis,
                                  ClientMetadata metadata) {
        UserToken token = new UserToken();
        token.setUser(user);
        token.setSession(session);
        token.setTokenType(UserToken.TokenType.REFRESH);
        token.setTokenHash(hashToken(refreshToken));
        token.setExpiredAt(LocalDateTime.now().plusSeconds(refreshValidityMillis));
        if (metadata != null) {
            token.setDeviceInfo(metadata.getDeviceInfo());
            token.setIpAddress(metadata.getIpAddress());
        }
        userTokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    public Optional<UserToken> findValidRefreshToken(String rawToken) {
        return findRefreshToken(rawToken)
                .filter(token -> token.getExpiredAt().isAfter(LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public Optional<UserToken> findRefreshToken(String rawToken) {
        return userTokenRepository.findByTokenHashAndTokenTypeAndRevokedFalse(
                hashToken(rawToken),
                UserToken.TokenType.REFRESH
        );
    }

    @Transactional
    public void revokeToken(UserToken token) {
        token.setRevoked(true);
        userTokenRepository.save(token);
    }

    @Transactional
    public void revokeSessionTokens(UserSession session) {
        List<UserToken> tokens = userTokenRepository.findBySessionAndRevokedFalse(session);
        tokens.forEach(token -> token.setRevoked(true));
        userTokenRepository.saveAll(tokens);
    }

    private String hashToken(String token) {
        return DigestUtils.md5DigestAsHex(token.getBytes(StandardCharsets.UTF_8));
    }
}

