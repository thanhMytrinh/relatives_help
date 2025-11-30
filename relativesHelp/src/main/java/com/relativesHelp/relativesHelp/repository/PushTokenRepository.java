package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    List<PushToken> findByUserId(Long userId);
    Optional<PushToken> findByUserIdAndToken(Long userId, String token);
    List<PushToken> findByUserIdIn(List<Long> userIds);
}