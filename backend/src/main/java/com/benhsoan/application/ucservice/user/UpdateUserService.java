package com.benhsoan.application.ucservice.user;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.auth.Role;
import com.benhsoan.domain.auth.User;
import com.benhsoan.domain.auth.exception.EmailAlreadyExistsException;
import com.benhsoan.domain.auth.exception.RoleNotFoundException;
import com.benhsoan.domain.auth.exception.UserNotFoundException;
import com.benhsoan.dto.request.user.UpdateUserCommand;
import com.benhsoan.dto.response.auth.UserResponse;
import com.benhsoan.port.inbound.user.UpdateUserUseCase;
import com.benhsoan.port.outbound.repository.RoleRepository;
import com.benhsoan.port.outbound.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserResponse update(
            UUID id,
            UpdateUserCommand command
    ) {

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!user.getEmail().equals(command.email())
                && userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException();
        }

        Role role = roleRepository.findByName(command.roleName())
                .orElseThrow(RoleNotFoundException::new);

        user.updateProfile(
                command.fullName(),
                command.email(),
                command.phone()
        );

        user = User.restore(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                role.getId(),
                user.isActive(),
                user.getLastLoginAt(),
                user.getCreatedAt()
        );

        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getPhone(),
                role.getName()
        );
    }
}