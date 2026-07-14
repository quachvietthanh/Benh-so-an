package com.benhsoan.service;

import com.benhsoan.dto.LoginRequest;
import com.benhsoan.dto.LoginResponse;
import com.benhsoan.model.entity.User;
import com.benhsoan.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(user.getUsername());

            return LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                    .build();
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }
}
