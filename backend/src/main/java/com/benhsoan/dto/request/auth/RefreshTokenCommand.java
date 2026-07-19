package com.benhsoan.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenCommand(

        @NotBlank
        String accessToken

){}
