package com.benhsoan.adapter.inbound.rest.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.benhsoan.adapter.inbound.rest.request.user.CreateUserRequest;
import com.benhsoan.adapter.inbound.rest.request.user.UpdateUserRequest;
import com.benhsoan.adapter.inbound.rest.response.user.UserResponse;
import com.benhsoan.dto.command.user.CreateUserCommand;
import com.benhsoan.dto.command.user.UpdateUserCommand;
import com.benhsoan.dto.result.user.UserResult;

@Component
public class UserRestMapper {

    public CreateUserCommand toCommand(CreateUserRequest request) {

        return new CreateUserCommand(
                request.username(),
                request.password(),
                request.fullName(),
                request.email(),
                request.phone(),
                request.roleName()
        );
    }

    public UpdateUserCommand toCommand(UpdateUserRequest request) {

        return new UpdateUserCommand(
                request.fullName(),
                request.email(),
                request.phone(),
                request.roleName()
        );
    }

    public UserResponse toResponse(UserResult result) {

        return new UserResponse(
                result.id(),
                result.username(),
                result.fullName(),
                result.email(),
                result.phone(),
                result.roleName()
        );
    }

    public List<UserResponse> toResponse(List<UserResult> results) {

        return results.stream()
                .map(this::toResponse)
                .toList();
    }
}