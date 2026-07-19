package com.benhsoan.port.inbound.auth;

import com.benhsoan.dto.request.auth.LoginCommand;
import com.benhsoan.dto.response.auth.LoginResponse;

public interface LoginUseCase {

    LoginResponse login(LoginCommand command);

}
