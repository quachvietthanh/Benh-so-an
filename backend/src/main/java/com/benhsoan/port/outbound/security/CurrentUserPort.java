package com.benhsoan.port.outbound.security;

import java.util.Set;
import java.util.UUID;

/**
 * Port for accessing the currently authenticated user
 * from the application layer without coupling to Spring Security.
 */
public interface CurrentUserPort {

    UUID getCurrentUserId();

    Set<String> getCurrentUserRoles();
}
