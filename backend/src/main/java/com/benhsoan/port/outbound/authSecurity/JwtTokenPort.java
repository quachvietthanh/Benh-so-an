package com.benhsoan.port.outbound.authSecurity;

import java.util.UUID;

public interface JwtTokenPort {

    String generateToken(
            UUID userId,
            String username,
            String role
    );

    UUID getUserId(String token);

    String getUsername(String token);

    String getRole(String token);

    boolean validate(String token);
}