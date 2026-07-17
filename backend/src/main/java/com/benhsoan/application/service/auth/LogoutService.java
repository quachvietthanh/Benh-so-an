package com.benhsoan.application.service.auth;

import com.benhsoan.port.inbound.auth.LogoutUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogoutService implements LogoutUseCase {

    @Override
    public void logout(String token) {
        log.debug("Logout request with token: {}", token);
    }
}
