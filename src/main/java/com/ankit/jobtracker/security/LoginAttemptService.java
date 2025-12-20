package com.ankit.jobtracker.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_SECONDS = 15 * 60; // 15 minutes

    private static class Attempt {
        int failures;
        Instant lockUntil;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    // Check if user is currently locked
    public boolean isLocked(String username) {
        Attempt attempt = attempts.get(username);
        if (attempt == null || attempt.lockUntil == null) {
            return false;
        }

        if (Instant.now().isAfter(attempt.lockUntil)) {
            // Auto-unlock
            attempts.remove(username);
            return false;
        }
        return true;
    }

    // Call on failed authentication
    public void loginFailed(String username) {
    Attempt attempt = attempts.computeIfAbsent(username, u -> new Attempt());
    attempt.failures++;

    System.out.println("FAILED LOGIN: " + username +
            " count=" + attempt.failures);

    if (attempt.failures >= MAX_FAILED_ATTEMPTS) {
        attempt.lockUntil = Instant.now().plusSeconds(LOCK_DURATION_SECONDS);
        System.out.println("ACCOUNT LOCKED: " + username);
    }
    }

    public boolean isCaptchaRequired(String username) {
    Attempt attempt = attempts.get(username);
    return attempt != null && attempt.failures >= 3;
    }


    // Call on successful login
    public void loginSucceeded(String username) {
        attempts.remove(username);
    }
}