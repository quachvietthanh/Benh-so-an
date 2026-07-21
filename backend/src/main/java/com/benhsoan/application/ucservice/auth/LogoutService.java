package com.benhsoan.application.ucservice.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.auth.UserSession;
import com.benhsoan.domain.auth.exception.SessionExpiredException;
import com.benhsoan.domain.auth.exception.TokenInvalidException;
import com.benhsoan.dto.command.auth.LogoutCommand;
import com.benhsoan.port.inbound.auth.LogoutUseCase;
import com.benhsoan.port.outbound.authSecurity.JwtTokenPort;
import com.benhsoan.port.outbound.authSecurity.TokenHashPort;
import com.benhsoan.port.outbound.repository.crudRepository.auth.UserSessionRepository;
import com.benhsoan.port.outbound.time.ClockPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutService implements LogoutUseCase {

    private final JwtTokenPort jwtTokenPort;

    private final TokenHashPort tokenHashPort;

    private final UserSessionRepository userSessionRepository;

    private final ClockPort clockPort;

    @Override
    public void logout(
            LogoutCommand command
    ) {

        String accessToken = command.accessToken();

        if (!jwtTokenPort.validate(accessToken)) {
            throw new TokenInvalidException();
        }

        String tokenHash =
                tokenHashPort.hash(accessToken);

        UserSession session =
                userSessionRepository.findByTokenHash(tokenHash)
                        .orElseThrow(TokenInvalidException::new);

        if (session.isRevoked()) {
            throw new SessionExpiredException();
        }

        if (session.isExpired(clockPort.now())) {
            throw new SessionExpiredException();
        }

        session.revoke(clockPort.now());

        userSessionRepository.save(session);
    }
}