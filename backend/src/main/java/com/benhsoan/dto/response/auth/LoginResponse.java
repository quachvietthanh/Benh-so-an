package com.benhsoan.dto.response.auth;

import java.util.UUID;

public record LoginResponse(

        UUID userId,

        String username,

        String accessToken,

        String role

) {
}