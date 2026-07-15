package com.benhsoan.common.security;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginAttemptService {

    private final int maxAttempts;
    private final long blockDurationMs;
    private final ConcurrentHashMap<String, LoginAttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    public LoginAttemptService(
            @Value("${app.security.login.max-attempts:5}") int maxAttempts,
            @Value("${app.security.login.block-duration-ms:900000}") long blockDurationMs) {
        this.maxAttempts = maxAttempts;
        this.blockDurationMs = blockDurationMs;
    }

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        attemptsCache.compute(key, (k, current) -> {
            LoginAttemptInfo info = (current == null) ? new LoginAttemptInfo() : current;
            info.attempts++;
            info.lastAttemptTime = Instant.now();
            return info;
        });
    }

    public boolean isBlocked(String key) {
        LoginAttemptInfo info = attemptsCache.get(key);
        if (info == null) {
            return false;
        }
        if (isBlockExpired(info)) {
            attemptsCache.remove(key);
            return false;
        }
        return info.attempts >= maxAttempts;
    }

    public int getAttemptCount(String key) {
        LoginAttemptInfo info = attemptsCache.get(key);
        return (info == null) ? 0 : info.attempts;
    }

    public void clearAll() {
        attemptsCache.clear();
    }

    private boolean isBlockExpired(LoginAttemptInfo info) {
        if (info.lastAttemptTime == null) {
            return true;
        }
        return Instant.now().isAfter(info.lastAttemptTime.plusMillis(blockDurationMs));
    }

    private static class LoginAttemptInfo {
        private int attempts;
        private Instant lastAttemptTime;

        LoginAttemptInfo() {
            this.attempts = 0;
            this.lastAttemptTime = Instant.now();
        }
    }
}
