package com.benhsoan.port.inbound.user;

import java.util.UUID;

import com.benhsoan.dto.command.user.UpdateUserCommand;
import com.benhsoan.dto.result.user.UserResult;

public interface UpdateUserUseCase {

    UserResult update( UUID id, UpdateUserCommand command );

}