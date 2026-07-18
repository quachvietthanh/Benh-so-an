package com.benhsoan.infrastructure.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginAttemptService — Brute Force Protection Tests")
class LoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;
    private static final String IP_KEY = "192.168.1.1";

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService(3, 60000);
    }

    @Test
    @DisplayName("New IP is not blocked")
    void newIpIsNotBlocked() {
        assertFalse(loginAttemptService.isBlocked(IP_KEY));
        assertEquals(0, loginAttemptService.getAttemptCount(IP_KEY));
    }

    @Test
    @DisplayName("After 3 failed attempts, IP is blocked")
    void ipBlockedAfterMaxAttempts() {
        loginAttemptService.loginFailed(IP_KEY);
        loginAttemptService.loginFailed(IP_KEY);
        loginAttemptService.loginFailed(IP_KEY);
        assertTrue(loginAttemptService.isBlocked(IP_KEY));
        assertEquals(3, loginAttemptService.getAttemptCount(IP_KEY));
    }

    @Test
    @DisplayName("After successful login, attempts are cleared")
    void successfulLoginClearsAttempts() {
        loginAttemptService.loginFailed(IP_KEY);
        loginAttemptService.loginFailed(IP_KEY);
        loginAttemptService.loginSucceeded(IP_KEY);
        assertFalse(loginAttemptService.isBlocked(IP_KEY));
        assertEquals(0, loginAttemptService.getAttemptCount(IP_KEY));
    }

    @Test
    @DisplayName("Two failed attempts does not block")
    void twoAttemptsNotBlocked() {
        loginAttemptService.loginFailed(IP_KEY);
        loginAttemptService.loginFailed(IP_KEY);
        assertFalse(loginAttemptService.isBlocked(IP_KEY));
    }

    @Test
    @DisplayName("Different IPs have independent counters")
    void differentIpsIndependent() {
        loginAttemptService.loginFailed(IP_KEY);
        loginAttemptService.loginFailed(IP_KEY);
        loginAttemptService.loginFailed(IP_KEY);

        String otherIp = "10.0.0.1";
        assertTrue(loginAttemptService.isBlocked(IP_KEY));
        assertFalse(loginAttemptService.isBlocked(otherIp));
        assertEquals(0, loginAttemptService.getAttemptCount(otherIp));
    }

    @Test
    @DisplayName("Block expires after duration")
    void blockExpiresAfterDuration() throws Exception {
        LoginAttemptService shortLived = new LoginAttemptService(1, 10);
        shortLived.loginFailed(IP_KEY);
        assertTrue(shortLived.isBlocked(IP_KEY));
        Thread.sleep(20);
        assertFalse(shortLived.isBlocked(IP_KEY));
    }

    @Test
    @DisplayName("Clear all resets all counters")
    void clearAllResets() {
        loginAttemptService.loginFailed(IP_KEY);
        loginAttemptService.loginFailed("10.0.0.1");
        loginAttemptService.clearAll();
        assertEquals(0, loginAttemptService.getAttemptCount(IP_KEY));
        assertEquals(0, loginAttemptService.getAttemptCount("10.0.0.1"));
        assertFalse(loginAttemptService.isBlocked(IP_KEY));
    }

    @Test
    @DisplayName("Username keys are treated separately from IP keys")
    void usernameKeysSeparate() {
        loginAttemptService.loginFailed("admin");
        loginAttemptService.loginFailed("admin");
        loginAttemptService.loginFailed("admin");

        assertTrue(loginAttemptService.isBlocked("admin"));
        assertFalse(loginAttemptService.isBlocked("doctor1"));
    }
}
