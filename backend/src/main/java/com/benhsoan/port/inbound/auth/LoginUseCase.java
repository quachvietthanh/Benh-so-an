package com.benhsoan.port.inbound.auth;

import com.benhsoan.dto.command.auth.LoginCommand;
import com.benhsoan.dto.result.auth.LoginResult;


public interface LoginUseCase {

    LoginResult login(LoginCommand command);

}