package com.benhsoan.application.service.auth;

import com.benhsoan.domain.auth.User;
import com.benhsoan.dto.request.auth.LoginCommand;
import com.benhsoan.dto.response.LoginResponse;
import com.benhsoan.infrastructure.security.service.LoginAttemptService;
import com.benhsoan.port.inbound.auth.LoginUseCase;
import com.benhsoan.port.outbound.authSecurity.JwtProviderPort;
import com.benhsoan.port.outbound.authSecurity.PasswordEncoderPort;
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
public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtProviderPort jwtProvider;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public LoginResponse login(LoginCommand command) {
        String username = command.getUsername();
        log.debug("Login attempt for user: {}", username);

        String attemptKey = "login:" + username;

        if (loginAttemptService.isBlocked(attemptKey)) {
            log.warn("Login blocked: too many failed attempts for '{}'", username);
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Tài khoản tạm thời bị khóa do nhập sai mật khẩu quá nhiều lần. Vui lòng thử lại sau 15 phút.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    loginAttemptService.loginFailed(attemptKey);
                    log.warn("Login failed: user '{}' not found", username);
                    return new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Tên đăng nhập hoặc mật khẩu không chính xác");
                });

        if (!user.isActive()) {
            loginAttemptService.loginFailed(attemptKey);
            log.warn("Login blocked: user '{}' is deactivated", username);
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên.");
        }

        if (user.isLocked()) {
            loginAttemptService.loginFailed(attemptKey);
            log.warn("Login blocked: user '{}' is locked", username);
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.");
        }

        if (!passwordEncoder.matches(command.getPassword(), user.getPasswordHash())) {
            loginAttemptService.loginFailed(attemptKey);
            log.warn("Login failed: invalid password for '{}'", username);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        loginAttemptService.loginSucceeded(attemptKey);
        user.updateLastLogin();
        userRepository.save(user);

        List<String> roles = List.of("USER");
        String token = jwtProvider.generateToken(username, roles);

        log.info("User '{}' logged in successfully", username);
        return LoginResponse.builder()
                .token(token)
                .username(username)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}
