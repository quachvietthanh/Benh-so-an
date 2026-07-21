package com.benhsoan.adapter.inbound.rest.mapper;

import org.springframework.stereotype.Component;

import com.benhsoan.adapter.inbound.rest.request.auth.LoginRequest;
import com.benhsoan.adapter.inbound.rest.request.auth.LogoutRequest;
import com.benhsoan.adapter.inbound.rest.request.auth.RefreshTokenRequest;
import com.benhsoan.adapter.inbound.rest.response.auth.LoginResponse;
import com.benhsoan.dto.command.auth.LoginCommand;
import com.benhsoan.dto.command.auth.LogoutCommand;
import com.benhsoan.dto.command.auth.RefreshTokenCommand;
import com.benhsoan.dto.result.auth.LoginResult;

@Component
public class AuthRestMapper {

    public LoginCommand toCommand(LoginRequest request) {

        return new LoginCommand(
                request.username(),
                request.password(),
                request.ipAddress()
        );
    }

    public LogoutCommand toCommand(LogoutRequest request) {

        return new LogoutCommand(
                request.accessToken()
        );
    }

    public RefreshTokenCommand toCommand(RefreshTokenRequest request) {

        return new RefreshTokenCommand(
                request.accessToken()
        );
    }

    public LoginResponse toResponse(LoginResult result) {

        return new LoginResponse(
                result.userId(),
                result.username(),
                result.accessToken(),
                result.role(),
                result.expiredAt()
        );
    }
}