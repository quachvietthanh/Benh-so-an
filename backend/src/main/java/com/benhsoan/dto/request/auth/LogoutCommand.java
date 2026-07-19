package com.benhsoan.dto.request.auth;


import jakarta.validation.constraints.NotBlank;

public record LogoutCommand(

        @NotBlank
        String accessToken

) {
}