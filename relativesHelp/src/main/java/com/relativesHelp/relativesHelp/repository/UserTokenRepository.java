package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.UserSession;
import com.relativesHelp.relativesHelp.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByTokenHashAndTokenTypeAndRevokedFalse(String tokenHash, UserToken.TokenType tokenType);

    List<UserToken> findBySessionAndRevokedFalse(UserSession session);
}

