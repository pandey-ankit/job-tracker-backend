package com.ankit.jobtracker.security;

import com.ankit.jobtracker.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {

    private final RefreshTokenRepository refreshTokenRepository;

    public SessionService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Logout from current device/session
     */
    @Transactional
    public void logoutCurrentSession(String refreshToken) {

        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    /**
     * Logout from ALL devices/sessions
     */
    @Transactional
    public void logoutAllSessions(String username) {

        refreshTokenRepository.deleteByUsername(username);
    }
}