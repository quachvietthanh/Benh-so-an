package com.benhsoan.adapter.inbound.rest.request.auth;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(

        @NotBlank
        String accessToken

) {
}