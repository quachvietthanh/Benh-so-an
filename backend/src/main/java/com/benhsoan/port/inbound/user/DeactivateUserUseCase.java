package com.benhsoan.port.inbound.user;

import java.util.UUID;

import com.benhsoan.dto.result.user.UserResult;

public interface DeactivateUserUseCase {

    UserResult deactivate(UUID id);

}