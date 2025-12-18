package com.ankit.jobtracker.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SECONDS = 60;

    private static class Attempt {
        int count;
        Instant windowStart;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public boolean allowRequest(String key) {
        Instant now = Instant.now();

        Attempt attempt = attempts.computeIfAbsent(key, k -> {
            Attempt a = new Attempt();
            a.count = 0;
            a.windowStart = now;
            return a;
        });

        synchronized (attempt) {
            if (now.isAfter(attempt.windowStart.plusSeconds(WINDOW_SECONDS))) {
                attempt.count = 0;
                attempt.windowStart = now;
            }

            attempt.count++;
            return attempt.count <= MAX_ATTEMPTS;
        }
    }
}