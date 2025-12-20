package com.ankit.jobtracker.security;

import com.ankit.jobtracker.entity.PasswordResetToken;
import com.ankit.jobtracker.entity.User;
import com.ankit.jobtracker.repository.PasswordResetTokenRepository;
import com.ankit.jobtracker.repository.RefreshTokenRepository;
import com.ankit.jobtracker.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RefreshTokenRepository refreshTokenRepository) {

        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * STEP 1: Generate reset token
     */
    public void createResetToken(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PasswordResetToken token = new PasswordResetToken();
        token.setUsername(user.getUsername());
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryTime(Instant.now().plusSeconds(15 * 60)); // 15 minutes
        token.setUsed(false);

        tokenRepository.save(token);

        // Simulate email sending
        System.out.println(
                "Password reset token for " + username + ": " + token.getToken()
        );
    }

    /**
     * STEP 2: Reset password using token
     */
    @Transactional
    public void resetPassword(String tokenValue, String newPassword) {

        PasswordResetToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (token.isUsed()) {
            throw new RuntimeException("Reset token already used");
        }

        if (token.getExpiryTime().isBefore(Instant.now())) {
            throw new RuntimeException("Reset token expired");
        }

        User user = userRepository.findByUsername(token.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔐 Encode & update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // ✅ Mark token as used
        token.setUsed(true);
        tokenRepository.save(token);

        // 🔥 Invalidate all existing sessions
        refreshTokenRepository.deleteByUsername(user.getUsername());
    }
}
