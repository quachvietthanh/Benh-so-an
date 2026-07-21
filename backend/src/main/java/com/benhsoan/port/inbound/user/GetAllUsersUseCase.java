package com.benhsoan.port.inbound.user;

import java.util.List;

import com.benhsoan.dto.result.user.UserResult;

public interface GetAllUsersUseCase {

    List<UserResult> getAll();

}