package com.benhsoan.port.inbound.auth;

import com.benhsoan.dto.command.auth.RefreshTokenCommand;
import com.benhsoan.dto.result.auth.LoginResult;

public interface RefreshTokenUseCase {

    LoginResult refreshToken( RefreshTokenCommand command);
}