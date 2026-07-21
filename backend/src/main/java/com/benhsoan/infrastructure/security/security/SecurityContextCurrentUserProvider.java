package com.benhsoan.infrastructure.security.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.benhsoan.domain.auth.User;
import com.benhsoan.port.outbound.security.CurrentUserProvider;

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

        if (principal instanceof User userDetails) {
            return userDetails.getId();
        }

        throw new IllegalStateException("Invalid authentication principal.");
    }

}
