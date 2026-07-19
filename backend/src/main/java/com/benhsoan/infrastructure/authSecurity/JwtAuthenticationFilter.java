package com.benhsoan.infrastructure.authSecurity;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.benhsoan.port.outbound.authSecurity.JwtTokenPort;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenPort jwtTokenPort;

    private static final List<String> PUBLIC_PATHS = List.of(
        "/auth/login",
        "/auth/register",
        "/auth/forgot-password",
        "/auth/reset-password",

        "/swagger-ui",
        "/swagger-ui.html",
        "/v3/api-docs",
        "/swagger-resources",
        "/webjars",

        "/actuator/health"
    );

    @Override
    protected boolean shouldNotFilter(
            @NonNull HttpServletRequest request
    ) {
        String path = request.getServletPath();

        return PUBLIC_PATHS.stream()
                .anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String token = extractToken(request);

            if (token != null && jwtTokenPort.validate(token)) {

                String username = jwtTokenPort.getUsername(token);

                String role = jwtTokenPort.getRole(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + role
                                        )
                                )
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception ex) {
            log.error(
                    "JWT authentication failed: {}",
                    ex.getMessage()
            );

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(
                request,
                response
        );
    }

    private String extractToken(
            HttpServletRequest request
    ) {
        String authorization =
                request.getHeader("Authorization");

        if (!StringUtils.hasText(authorization)) {
            return null;
        }

        if (!authorization.startsWith("Bearer ")) {
            return null;
        }

        String token = authorization.substring(7);

        return StringUtils.hasText(token)
                ? token
                : null;
    }
}
