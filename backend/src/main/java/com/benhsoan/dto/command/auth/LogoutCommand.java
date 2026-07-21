package com.benhsoan.dto.command.auth;


import jakarta.validation.constraints.NotBlank;

public record LogoutCommand(

        @NotBlank
        String accessToken

) {
}