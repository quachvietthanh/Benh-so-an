package com.benhsoan.application.service.auth;

import com.benhsoan.domain.auth.User;
import com.benhsoan.dto.request.auth.RefreshTokenCommand;
import com.benhsoan.dto.response.LoginResponse;
import com.benhsoan.port.inbound.auth.RefreshTokenUseCase;
import com.benhsoan.port.outbound.authSecurity.JwtProviderPort;
import com.benhsoan.port.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final JwtProviderPort jwtProvider;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LoginResponse refreshToken(RefreshTokenCommand command) {
        String oldToken = command.getRefreshToken();
        log.debug("Refresh token request");

        if (oldToken == null || !jwtProvider.validateToken(oldToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token không hợp lệ hoặc đã hết hạn");
        }

        String username = jwtProvider.getUsernameFromToken(oldToken);
        List<String> roles = jwtProvider.getRolesFromToken(oldToken);

        jwtProvider.invalidateToken(oldToken);

        String newToken = jwtProvider.generateToken(username, roles);

        User user = userRepository.findByUsername(username).orElse(null);

        return LoginResponse.builder()
                .token(newToken)
                .username(username)
                .fullName(user != null ? user.getFullName() : null)
                .email(user != null ? user.getEmail() : null)
                .roles(roles)
                .build();
    }
}
