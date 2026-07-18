package com.benhsoan.adapter.inbound.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.benhsoan.dto.request.user.CreateUserCommand;
import com.benhsoan.dto.request.user.UpdateUserCommand;
import com.benhsoan.dto.response.auth.UserResponse;
import com.benhsoan.port.inbound.user.ActivateUserUseCase;
import com.benhsoan.port.inbound.user.CreateUserUseCase;
import com.benhsoan.port.inbound.user.DeactivateUserUseCase;
import com.benhsoan.port.inbound.user.DeleteUserUseCase;
import com.benhsoan.port.inbound.user.GetAllUsersUseCase;
import com.benhsoan.port.inbound.user.GetUserUseCase;
import com.benhsoan.port.inbound.user.UpdateUserUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetAllUsersUseCase getUsersUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final DeactivateUserUseCase deactivateUserUseCase;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse create(
            @Valid @RequestBody CreateUserCommand command
    ) {
        return createUserUseCase.createUser(command);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        return getUsersUseCase.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getById(
            @PathVariable UUID id
    ) {
        return getUserUseCase.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserCommand command
    ) {
        return updateUserUseCase.update(id, command);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id
    ) {
        deleteUserUseCase.delete(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    public UserResponse activate(
        @PathVariable UUID id
    ) {
        return activateUserUseCase.activate(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public UserResponse deactivate(
        @PathVariable UUID id
    ) {
        return deactivateUserUseCase.deactivate(id);
    }
}