package com.benhsoan.application.ucservice.auth;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.auth.Role;
import com.benhsoan.domain.auth.User;
import com.benhsoan.domain.auth.UserSession;
import com.benhsoan.domain.auth.exception.AccountDisabledException;
import com.benhsoan.domain.auth.exception.SessionExpiredException;
import com.benhsoan.domain.auth.exception.TokenInvalidException;
import com.benhsoan.domain.auth.exception.UserNotFoundException;
import com.benhsoan.dto.request.auth.RefreshTokenCommand;
import com.benhsoan.dto.response.auth.LoginResponse;
import com.benhsoan.port.inbound.auth.RefreshTokenUseCase;
import com.benhsoan.port.outbound.authSecurity.JwtTokenPort;
import com.benhsoan.port.outbound.authSecurity.TokenHashPort;
import com.benhsoan.port.outbound.repository.RoleRepository;
import com.benhsoan.port.outbound.repository.UserRepository;
import com.benhsoan.port.outbound.repository.UserSessionRepository;
import com.benhsoan.port.outbound.time.ClockPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService implements RefreshTokenUseCase {

    private static final Duration SESSION_TIMEOUT =
            Duration.ofMinutes(30);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserSessionRepository userSessionRepository;

    private final JwtTokenPort jwtTokenPort;

    private final TokenHashPort tokenHashPort;

    private final ClockPort clockPort;

    @Override
    public LoginResponse refreshToken(
            RefreshTokenCommand command
    ) {

        String oldToken = command.accessToken();

        if (!jwtTokenPort.validate(oldToken)) {
            throw new TokenInvalidException();
        }

        String tokenHash =
                tokenHashPort.hash(oldToken);

        UserSession session =
                userSessionRepository.findByTokenHash(tokenHash)
                        .orElseThrow(TokenInvalidException::new);

        Instant now = clockPort.now();

        if (!session.isActive(now, SESSION_TIMEOUT)) {
            throw new SessionExpiredException();
        }

        User user =
                userRepository.findById(session.getUserId())
                        .orElseThrow(UserNotFoundException::new);

        if (!user.isActive()) {
            throw new AccountDisabledException();
        }

        Role role =
                roleRepository.findById(user.getRoleId())
                        .orElseThrow(IllegalStateException::new);

        String newToken =
                jwtTokenPort.generateToken(
                        user.getId(),
                        user.getUsername(),
                        role.getName()
                );

        String newTokenHash =
                tokenHashPort.hash(newToken);

        session.refresh(SESSION_TIMEOUT);

        userSessionRepository.deleteById(session.getId());

        UserSession newSession =
                UserSession.create(
                        user.getId(),
                        newTokenHash,
                        jwtTokenPort.getExpiredAt(newToken)
                );

        userSessionRepository.save(newSession);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                newToken,
                role.getName(),
                jwtTokenPort.getExpiredAt(newToken)
        );
    }
}