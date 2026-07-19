package com.benhsoan.port.outbound.authSecurity;

public interface LoginAttemptPort {

    void loginSucceeded(String username);

    void loginFailed(String username);

    boolean isBlocked(String username);

    int getAttemptCount(String username);
}