package com.relativesHelp.relativesHelp.security.service;

import com.relativesHelp.relativesHelp.entity.User;
import com.relativesHelp.relativesHelp.entity.UserSession;
import com.relativesHelp.relativesHelp.repository.UserSessionRepository;
import com.relativesHelp.relativesHelp.security.dto.ClientMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSessionService {
    private final UserSessionRepository userSessionRepository;

    @Transactional
    public UserSession createSession(User user, long refreshValidityMillis, ClientMetadata metadata) {
        UserSession session = new UserSession();
        session.setUser(user);
        session.setSessionId(UUID.randomUUID().toString());
        session.setDeviceName(metadata != null ? metadata.getDeviceInfo() : null);
        session.setBrowser(metadata != null ? metadata.getUserAgent() : null);
        session.setIpAddress(metadata != null ? metadata.getIpAddress() : null);
        session.setExpiredAt(LocalDateTime.now().plusSeconds(refreshValidityMillis));
        session.setLastActivityAt(LocalDateTime.now());
        return userSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public Optional<UserSession> findValidSession(String sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }
        return userSessionRepository.findBySessionId(sessionId)
                .filter(UserSession::isActive)
                .filter(session -> session.getExpiredAt().isAfter(LocalDateTime.now()))
                .map(session -> {
                    // Force initialize user reference before leaving transactional context
                    session.getUser().getId();
                    return session;
                });
    }

    @Transactional
    public void touchSession(UserSession session) {
        session.setLastActivityAt(LocalDateTime.now());
        userSessionRepository.save(session);
    }

    @Transactional
    public void extendSession(UserSession session, long refreshValidityMillis) {
        session.setExpiredAt(LocalDateTime.now().plusSeconds(refreshValidityMillis));
        userSessionRepository.save(session);
    }

    @Transactional
    public void deactivateSession(UserSession session) {
        session.setActive(false);
        userSessionRepository.save(session);
    }
}

