package com.benhsoan.application.service.auth;

import com.benhsoan.dto.request.auth.LoginCommand;
import com.benhsoan.dto.response.LoginResponse;
import com.benhsoan.port.inbound.auth.LoginUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginService implements LoginUseCase {

    @Override
    public LoginResponse login(LoginCommand command) {
        log.debug("Login attempt for user: {}", command.getUsername());
        return LoginResponse.builder()
                .token("placeholder-token")
                .build();
    }
}
