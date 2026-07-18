package com.benhsoan.infrastructure.security.adapter;

import com.benhsoan.port.outbound.authSecurity.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder delegate;

    public PasswordEncoderAdapter() {
        this.delegate = new BCryptPasswordEncoder(12);
    }

    @Override
    public String encode(String rawPassword) {
        return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return delegate.matches(rawPassword, encodedPassword);
    }
}
