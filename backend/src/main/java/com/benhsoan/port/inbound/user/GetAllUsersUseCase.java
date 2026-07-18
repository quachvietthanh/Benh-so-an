package com.benhsoan.port.inbound.user;

import java.util.List;

import com.benhsoan.dto.response.auth.UserResponse;

public interface GetAllUsersUseCase {

    List<UserResponse> getAll();

}