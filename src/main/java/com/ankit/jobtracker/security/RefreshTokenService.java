package com.ankit.jobtracker.security;

import com.ankit.jobtracker.entity.RefreshToken;
import com.ankit.jobtracker.entity.User;

import com.ankit.jobtracker.repository.RefreshTokenRepository;
import com.ankit.jobtracker.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            JwtUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public String refreshAccessToken(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // 🚨 Reuse detection
        if (refreshToken.isRevoked()) {
            refreshTokenRepository.deleteByUsername(refreshToken.getUsername());
            throw new RuntimeException("Refresh token reuse detected");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        // 🔄 Rotate token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        RefreshToken newToken = new RefreshToken();
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setUsername(refreshToken.getUsername());
        newToken.setExpiryDate(
                Instant.now().plusSeconds(7 * 24 * 60 * 60)
        );
        refreshTokenRepository.save(newToken);

        // ✅ DB IS SOURCE OF TRUTH
        User user = userRepository.findByUsername(
                refreshToken.getUsername()
        ).orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtil.generateTokenFromUser(user);
    }
}
