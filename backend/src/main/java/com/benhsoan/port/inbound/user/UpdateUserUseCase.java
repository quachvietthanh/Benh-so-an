package com.benhsoan.port.inbound.user;

import java.util.UUID;

import com.benhsoan.dto.request.user.UpdateUserCommand;
import com.benhsoan.dto.response.auth.UserResponse;

public interface UpdateUserUseCase {

    UserResponse update(
            UUID id,
            UpdateUserCommand command
    );

}