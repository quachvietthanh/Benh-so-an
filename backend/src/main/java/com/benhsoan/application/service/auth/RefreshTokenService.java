package com.benhsoan.application.service.auth;

import com.benhsoan.dto.request.auth.RefreshTokenCommand;
import com.benhsoan.dto.response.LoginResponse;
import com.benhsoan.port.inbound.auth.RefreshTokenUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    @Override
    public LoginResponse refreshToken(RefreshTokenCommand command) {
        log.debug("Refresh token request: {}", command.getRefreshToken());
        return LoginResponse.builder()
                .token("placeholder-refreshed-token")
                .build();
    }
}
