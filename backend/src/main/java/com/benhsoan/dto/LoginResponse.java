package com.benhsoan.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = lombok.AccessLevel.PACKAGE)
@Builder
public class LoginResponse {

    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private String username;
    private String fullName;
    private String email;
    private Set<String> roles;

    public LoginResponse(String token, String username, String fullName, String email, Set<String> roles) {
        this.token = token;
        this.tokenType = "Bearer";
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }
}
