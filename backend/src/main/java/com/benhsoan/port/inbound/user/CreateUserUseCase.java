package com.benhsoan.port.inbound.user;

import com.benhsoan.dto.request.user.CreateUserCommand;
import com.benhsoan.dto.response.auth.UserResponse;

public interface CreateUserUseCase {

    UserResponse createUser(
            CreateUserCommand command
    );

}