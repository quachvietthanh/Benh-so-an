package com.benhsoan.port.inbound.auth;

import com.benhsoan.dto.request.auth.CreateUserCommand;
import com.benhsoan.dto.response.auth.UserResponse;

public interface CreateUserUseCase {

    UserResponse createUser(
            CreateUserCommand command
    );

}