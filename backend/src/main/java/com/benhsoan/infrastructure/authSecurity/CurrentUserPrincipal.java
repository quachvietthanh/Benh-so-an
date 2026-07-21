package com.benhsoan.infrastructure.authSecurity;

import java.util.UUID;

public record CurrentUserPrincipal(
        UUID userId,
        String username
) {
}