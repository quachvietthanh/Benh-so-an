package com.benhsoan.application.ucservice.auth;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.auth.Role;
import com.benhsoan.domain.auth.User;
import com.benhsoan.domain.auth.UserSession;
import com.benhsoan.domain.auth.exception.AccountDisabledException;
import com.benhsoan.domain.auth.exception.InvalidPasswordException;
import com.benhsoan.domain.auth.exception.TooManyLoginAttemptsException;
import com.benhsoan.domain.auth.exception.UserNotFoundException;
import com.benhsoan.dto.request.auth.LoginCommand;
import com.benhsoan.dto.response.auth.LoginResponse;
import com.benhsoan.port.inbound.auth.LoginUseCase;
import com.benhsoan.port.outbound.authSecurity.JwtTokenPort;
import com.benhsoan.port.outbound.authSecurity.LoginAttemptPort;
import com.benhsoan.port.outbound.authSecurity.PasswordEncoderPort;
import com.benhsoan.port.outbound.authSecurity.TokenHashPort;
import com.benhsoan.port.outbound.repository.crudRepository.auth.RoleRepository;
import com.benhsoan.port.outbound.repository.crudRepository.auth.UserRepository;
import com.benhsoan.port.outbound.repository.crudRepository.auth.UserSessionRepository;
import com.benhsoan.port.outbound.time.ClockPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService implements LoginUseCase {

    private static final Duration SESSION_TIMEOUT =
            Duration.ofMinutes(30);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserSessionRepository userSessionRepository;

    private final PasswordEncoderPort passwordEncoderPort;

    private final JwtTokenPort jwtTokenPort;

    private final TokenHashPort tokenHashPort;

    private final LoginAttemptPort loginAttemptPort;

    private final ClockPort clockPort;

    @Override
    public LoginResponse login(LoginCommand command) {

        String username = command.username();

        if (loginAttemptPort.isBlocked(username)) {
            throw new TooManyLoginAttemptsException();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {

                    loginAttemptPort.loginFailed(username);

                    return new UserNotFoundException();
                });

        if (!user.isActive()) {
            throw new AccountDisabledException();
        }

        if (!passwordEncoderPort.matches(
                command.password(),
                user.getPasswordHash()
        )) {

            loginAttemptPort.loginFailed(username);

            throw new InvalidPasswordException();
        }

        loginAttemptPort.loginSucceeded(username);

        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(IllegalStateException::new);

        String accessToken =
                jwtTokenPort.generateToken(
                        user.getId(),
                        user.getUsername(),
                        role.getName()
                );

        String tokenHash =
                tokenHashPort.hash(accessToken);

        Instant now = clockPort.now();

        Instant expiredAt =
                jwtTokenPort.getExpiredAt(accessToken);

        userSessionRepository.deleteByUserId(user.getId());

        UserSession session =
                UserSession.create(
                        user.getId(),
                        tokenHash,
                        expiredAt
                );

        userSessionRepository.save(session);

        user.updateLastLogin(now);

        userRepository.save(user);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                accessToken,
                role.getName(),
                expiredAt
        );
    }
}