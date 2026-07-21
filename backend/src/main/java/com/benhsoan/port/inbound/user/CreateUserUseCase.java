package com.benhsoan.port.inbound.user;

import com.benhsoan.dto.command.user.CreateUserCommand;
import com.benhsoan.dto.result.user.UserResult;

public interface CreateUserUseCase {

    UserResult createUser(
            CreateUserCommand command
    );

}