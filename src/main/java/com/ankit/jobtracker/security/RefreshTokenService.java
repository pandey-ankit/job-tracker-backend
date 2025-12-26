package com.ankit.jobtracker.security;

import com.ankit.jobtracker.entity.RefreshToken;
import com.ankit.jobtracker.entity.User;
import com.ankit.jobtracker.exception.InvalidRefreshTokenException;
import com.ankit.jobtracker.repository.RefreshTokenRepository;
import com.ankit.jobtracker.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_EXPIRY_SECONDS = 7 * 24 * 60 * 60; // 7 days

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            JwtUtil jwtUtil
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public RefreshToken createRefreshToken(String username) {
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUsername(username);
        token.setExpiryDate(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRY_SECONDS));
        token.setRevoked(false);

        return refreshTokenRepository.save(token);
    }

    // ✅ SINGLE SOURCE OF TRUTH
    public RefreshToken validateRefreshToken(String tokenValue) {

        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (token.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token revoked");
        }

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        return token;
    }

    public void revokeToken(String tokenValue) {
        refreshTokenRepository.findByToken(tokenValue)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    // ✅ FIXED: reuse validation logic
    public String refreshAccessToken(String refreshTokenValue) {

        RefreshToken refreshToken = validateRefreshToken(refreshTokenValue);

        String username = refreshToken.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)
                );

        return jwtUtil.generateToken(user);
    }

    @Transactional
    public void deleteAllTokensForUser(String username) {
        refreshTokenRepository.deleteByUsername(username);
        System.out.println(">>> Deleted all refresh tokens for user: " + username);
    }

}