package com.benhsoan.adapter.inbound.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.benhsoan.adapter.inbound.rest.mapper.AuthRestMapper;
import com.benhsoan.adapter.inbound.rest.request.auth.LoginRequest;
import com.benhsoan.adapter.inbound.rest.request.auth.LogoutRequest;
import com.benhsoan.adapter.inbound.rest.request.auth.RefreshTokenRequest;
import com.benhsoan.adapter.inbound.rest.response.auth.LoginResponse;
import com.benhsoan.port.dto.result.LoginResult;
import com.benhsoan.port.inbound.auth.LoginUseCase;
import com.benhsoan.port.inbound.auth.LogoutUseCase;
import com.benhsoan.port.inbound.auth.RefreshTokenUseCase;

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

    private final AuthRestMapper authRestMapper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResult result =
                loginUseCase.login(
                        authRestMapper.toCommand(request));

        return ResponseEntity.ok(
                authRestMapper.toResponse(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutRequest request
    ) {

        logoutUseCase.logout(
                authRestMapper.toCommand(request));

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        LoginResult result =
                refreshTokenUseCase.refreshToken(
                        authRestMapper.toCommand(request));

        return ResponseEntity.ok(
                authRestMapper.toResponse(result));
    }
}