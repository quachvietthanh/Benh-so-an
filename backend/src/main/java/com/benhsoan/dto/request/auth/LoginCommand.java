package com.benhsoan.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginCommand(

        @NotBlank
        String username,

        @NotBlank
        String password,

        String ipAddress

){}
