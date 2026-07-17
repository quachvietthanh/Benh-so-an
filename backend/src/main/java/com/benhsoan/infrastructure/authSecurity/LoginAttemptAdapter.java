package com.benhsoan.infrastructure.authSecurity;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.benhsoan.port.outbound.authSecurity.LoginAttemptPort;

@Component
public class LoginAttemptAdapter implements LoginAttemptPort {

    private final int maxAttempts;

    private final long blockDurationMs;

    private final ConcurrentHashMap<String, LoginAttemptInfo> cache =
            new ConcurrentHashMap<>();

    public LoginAttemptAdapter(
            @Value("${app.security.login.max-attempts:5}") int maxAttempts,
            @Value("${app.security.login.block-duration-ms:900000}") long blockDurationMs
    ) {
        this.maxAttempts = maxAttempts;
        this.blockDurationMs = blockDurationMs;
    }

    @Override
    public void loginSucceeded(String username) {
        cache.remove(username);
    }

    @Override
    public void loginFailed(String username) {

        cache.compute(username, (k, current) -> {

            LoginAttemptInfo info =
                    current == null
                    ? new LoginAttemptInfo()
                    : current;

            info.attempts++;

            info.lastAttempt = Instant.now();

            return info;
        });
    }

    @Override
    public boolean isBlocked(String username) {

        LoginAttemptInfo info = cache.get(username);

        if (info == null) {
            return false;
        }

        if (Instant.now().isAfter(
                info.lastAttempt.plusMillis(blockDurationMs))) {

            cache.remove(username);

            return false;
        }

        return info.attempts >= maxAttempts;
    }

    @Override
    public int getAttemptCount(String username) {

        LoginAttemptInfo info = cache.get(username);

        return info == null
                ? 0
                : info.attempts;
    }

    private static class LoginAttemptInfo {

        int attempts;

        Instant lastAttempt;

        LoginAttemptInfo() {

            attempts = 0;

            lastAttempt = Instant.now();
        }
    }

}