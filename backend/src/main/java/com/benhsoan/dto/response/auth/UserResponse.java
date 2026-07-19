package com.benhsoan.dto.response.auth;

import java.util.UUID;

public record UserResponse(

        UUID id,

        String username,

        String fullName,

        String email,

        String phone,

        String role

) {
}