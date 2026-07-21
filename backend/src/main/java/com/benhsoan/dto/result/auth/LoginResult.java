package com.benhsoan.dto.result.auth;

import java.time.Instant;
import java.util.UUID;

public record LoginResult(

        UUID userId,

        String username,

        String accessToken,

        String role,

        Instant expiredAt

) {
}