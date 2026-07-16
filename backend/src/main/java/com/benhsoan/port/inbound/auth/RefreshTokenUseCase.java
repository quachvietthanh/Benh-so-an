package com.benhsoan.port.inbound.auth;

import com.benhsoan.dto.request.auth.RefreshTokenCommand;
import com.benhsoan.dto.response.LoginResponse;

public interface RefreshTokenUseCase {
    LoginResponse refreshToken(RefreshTokenCommand command);
}
