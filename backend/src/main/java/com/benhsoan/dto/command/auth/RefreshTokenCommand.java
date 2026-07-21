package com.benhsoan.dto.command.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenCommand(

        @NotBlank
        String accessToken

){}