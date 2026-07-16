package com.benhsoan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;

    private UUID userId;
    private String username;
    private String fullName;
    private String email;

    private List<String> roles;
    private Set<String> permissions;

    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }
}
