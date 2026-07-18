package com.benhsoan.application.service.auth;

import com.benhsoan.port.inbound.auth.LogoutUseCase;
import com.benhsoan.port.outbound.authSecurity.JwtProviderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {

    private final JwtProviderPort jwtProvider;

    @Override
    public void logout(String token) {
        if (token != null) {
            jwtProvider.invalidateToken(token);
            log.debug("Token invalidated on logout");
        }
    }
}
