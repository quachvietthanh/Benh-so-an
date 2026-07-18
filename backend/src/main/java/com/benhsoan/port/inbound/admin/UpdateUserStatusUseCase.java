package com.benhsoan.port.inbound.admin;

import java.util.Map;
import java.util.UUID;

public interface UpdateUserStatusUseCase {
    Map<String, Object> updateUserStatus(UUID userId, boolean locked, String adminUsername);
}
