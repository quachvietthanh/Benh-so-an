package com.benhsoan.port.inbound.user;

import java.util.UUID;

import com.benhsoan.dto.response.auth.UserResponse;

public interface DeactivateUserUseCase {

    UserResponse deactivate(UUID id);

}