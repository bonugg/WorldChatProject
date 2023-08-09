package com.example.WorldChatProject.user.security.repository;

import com.example.WorldChatProject.user.security.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    boolean existsByKeyId(String keyId);
    void deleteByKeyId(String keyId);
    Optional<RefreshToken> findByKeyId(String keyId);
}