package com.benhsoan.infrastructure.security.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.benhsoan.infrastructure.authSecurity.CurrentUserPrincipal;
import com.benhsoan.port.outbound.security.CurrentUserProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SecurityContextCurrentUserProvider
        implements CurrentUserProvider {

    @Override
    public UUID getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        log.info("Authentication class = {}",
        authentication.getClass().getName());

        log.info("Principal class = {}",
        principal.getClass().getName());

        log.info("Principal value = {}",
        principal);
        
        if (principal instanceof CurrentUserPrincipal currentUser) {
            return currentUser.userId();
        }

        throw new IllegalStateException("Invalid authentication principal.");
    }
}