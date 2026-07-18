package com.benhsoan.infrastructure.security;

import com.benhsoan.infrastructure.security.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginAttemptService - Brute Force Protection Tests")
class LoginAttemptServiceTest {

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MS = 900_000; // 15 minutes

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService(MAX_ATTEMPTS, BLOCK_DURATION_MS);
    }

    @Test
    @DisplayName("New IP should not be blocked")
    void newIpNotBlocked() {
        assertFalse(service.isBlocked("192.168.1.1"));
        assertEquals(0, service.getAttemptCount("192.168.1.1"));
    }

    @Test
    @DisplayName("Should block after max failed attempts")
    void blockAfterMaxFailures() {
        String ip = "10.0.0.1";

        // 5 failed attempts
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            assertFalse(service.isBlocked(ip), "Should not be blocked at attempt " + (i + 1));
            service.loginFailed(ip);
        }

        // 6th attempt - should be blocked
        assertTrue(service.isBlocked(ip));
        assertEquals(MAX_ATTEMPTS, service.getAttemptCount(ip));
    }

    @Test
    @DisplayName("Should not block with fewer than max attempts")
    void notBlockBelowMaxAttempts() {
        String ip = "10.0.0.2";

        for (int i = 0; i < MAX_ATTEMPTS - 1; i++) {
            service.loginFailed(ip);
            assertFalse(service.isBlocked(ip));
        }

        assertEquals(MAX_ATTEMPTS - 1, service.getAttemptCount(ip));
    }

    @Test
    @DisplayName("Successful login should clear attempts")
    void successClearsAttempts() {
        String ip = "10.0.0.3";

        service.loginFailed(ip);
        service.loginFailed(ip);
        assertEquals(2, service.getAttemptCount(ip));

        service.loginSucceeded(ip);
        assertEquals(0, service.getAttemptCount(ip));
        assertFalse(service.isBlocked(ip));
    }

    @Test
    @DisplayName("Block should expire after duration")
    void blockExpiresAfterDuration() throws InterruptedException {
        String ip = "10.0.0.4";
        long shortBlockMs = 100; // 100ms for testing

        LoginAttemptService shortService = new LoginAttemptService(2, shortBlockMs);

        shortService.loginFailed(ip);
        shortService.loginFailed(ip);
        assertTrue(shortService.isBlocked(ip));

        // Wait for block to expire
        Thread.sleep(shortBlockMs + 50);

        assertFalse(shortService.isBlocked(ip));
        assertEquals(0, shortService.getAttemptCount(ip));
    }

    @Test
    @DisplayName("Should handle multiple IPs independently")
    void multipleIpsIndependent() {
        String ip1 = "10.0.0.10";
        String ip2 = "10.0.0.20";

        service.loginFailed(ip1);
        service.loginFailed(ip1);
        service.loginFailed(ip1);
        service.loginFailed(ip1);
        service.loginFailed(ip1); // 5 attempts

        assertTrue(service.isBlocked(ip1));
        assertFalse(service.isBlocked(ip2));

        service.loginFailed(ip2);
        assertEquals(1, service.getAttemptCount(ip2));
    }

    @Test
    @DisplayName("ClearAll should reset all entries")
    void clearAllResetsEverything() {
        service.loginFailed("ip1");
        service.loginFailed("ip2");
        service.loginFailed("ip3");

        service.clearAll();

        assertEquals(0, service.getAttemptCount("ip1"));
        assertEquals(0, service.getAttemptCount("ip2"));
        assertEquals(0, service.getAttemptCount("ip3"));
        assertFalse(service.isBlocked("ip1"));
    }
}
