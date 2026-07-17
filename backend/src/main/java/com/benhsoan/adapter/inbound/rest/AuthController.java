package com.benhsoan.adapter.inbound.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.benhsoan.dto.request.auth.LoginCommand;
import com.benhsoan.dto.request.auth.LogoutCommand;
import com.benhsoan.dto.request.auth.RefreshTokenCommand;
import com.benhsoan.dto.response.auth.LoginResponse;
import com.benhsoan.port.inbound.auth.LoginUseCase;
import com.benhsoan.port.inbound.auth.LogoutUseCase;
import com.benhsoan.port.inbound.auth.RefreshTokenUseCase;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final LoginUseCase loginUseCase;

    private final LogoutUseCase logoutUseCase;

    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginCommand command
    ) {
            System.out.println("LOGIN CONTROLLER");

        LoginResponse response =
                loginUseCase.login(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutCommand command
    ) {

        logoutUseCase.logout(command);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenCommand command
    ) {

        LoginResponse response =
                refreshTokenUseCase.refreshToken(command);

        return ResponseEntity.ok(response);
    }
    
    
    @PostConstruct
    public void init() {
        System.out.println("AUTH CONTROLLER CREATED");
}

}