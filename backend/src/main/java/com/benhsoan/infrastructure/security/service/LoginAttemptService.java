package com.benhsoan.infrastructure.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LoginAttemptService {

    private final int maxAttempts;
    private final long blockDurationMs;
    private final ConcurrentHashMap<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    public LoginAttemptService(
            @Value("${app.security.login.max-attempts:5}") int maxAttempts,
            @Value("${app.security.login.block-duration-ms:900000}") long blockDurationMs
    ) {
        this.maxAttempts = maxAttempts;
        this.blockDurationMs = blockDurationMs;
    }

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        attemptsCache.compute(key, (k, info) -> {
            AttemptInfo current = (info == null) ? new AttemptInfo() : info;
            current.attempts++;
            current.lastAttempt = Instant.now();
            return current;
        });
    }

    public boolean isBlocked(String key) {
        AttemptInfo info = attemptsCache.get(key);
        if (info == null) {
            return false;
        }

        if (info.lastAttempt != null &&
                Instant.now().isAfter(info.lastAttempt.plusMillis(blockDurationMs))) {
            attemptsCache.remove(key);
            return false;
        }

        return info.attempts >= maxAttempts;
    }

    public int getAttemptCount(String key) {
        AttemptInfo info = attemptsCache.get(key);
        return (info == null) ? 0 : info.attempts;
    }

    public void clearAll() {
        attemptsCache.clear();
    }

    private static class AttemptInfo {
        int attempts;
        Instant lastAttempt;

        AttemptInfo() {
            this.attempts = 0;
            this.lastAttempt = Instant.now();
        }
    }
}
