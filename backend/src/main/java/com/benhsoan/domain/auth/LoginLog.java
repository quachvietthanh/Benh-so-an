package com.benhsoan.domain.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginLog {

    private UUID id;

    private UUID userId;

    private String username;

    private String ipAddress;

    private String userAgent;

    private LoginStatus status;

    private String failureReason;

    private Instant loginAt;

    public enum LoginStatus {
        SUCCESS,
        FAILED_INVALID_CREDENTIALS,
        FAILED_ACCOUNT_LOCKED,
        FAILED_ACCOUNT_DISABLED,
        FAILED_TOKEN_EXPIRED
    }

    public static LoginLog success(UUID userId, String username, String ipAddress, String userAgent) {
        return LoginLog.builder()
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status(LoginStatus.SUCCESS)
                .loginAt(Instant.now())
                .build();
    }

    public static LoginLog failure(String username, String ipAddress, String userAgent, LoginStatus status, String reason) {
        return LoginLog.builder()
                .username(username)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status(status)
                .failureReason(reason)
                .loginAt(Instant.now())
                .build();
    }
}
