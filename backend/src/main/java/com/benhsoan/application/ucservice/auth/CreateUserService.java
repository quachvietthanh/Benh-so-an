package com.benhsoan.application.ucservice.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.auth.Role;
import com.benhsoan.domain.auth.User;
import com.benhsoan.domain.auth.exception.EmailAlreadyExistsException;
import com.benhsoan.domain.auth.exception.RoleNotFoundException;
import com.benhsoan.domain.auth.exception.UserAlreadyExistsException;
import com.benhsoan.dto.request.auth.CreateUserCommand;
import com.benhsoan.dto.response.auth.UserResponse;
import com.benhsoan.port.inbound.auth.CreateUserUseCase;
import com.benhsoan.port.outbound.authSecurity.PasswordEncoderPort;
import com.benhsoan.port.outbound.repository.RoleRepository;
import com.benhsoan.port.outbound.repository.UserRepository;
import com.benhsoan.port.outbound.time.ClockPort;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoderPort passwordEncoder;

    private final ClockPort clockPort;

    @Override
    public UserResponse createUser(
            CreateUserCommand command
    ) {

        if (userRepository.existsByUsername(command.username())) {
            throw new UserAlreadyExistsException();
        }

        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException();
        }

        Role role = roleRepository.findByName(command.roleName())
                .orElseThrow(RoleNotFoundException::new);

        String passwordHash =
                passwordEncoder.encode(command.password());

        User user = User.create(
                command.username(),
                passwordHash,
                command.fullName(),
                command.email(),
                command.phone(),
                role.getId()
        );

        /*
         * Đồng bộ thời gian tạo theo ClockPort
         */
        user = User.restore(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRoleId(),
                user.isActive(),
                user.getLastLoginAt(),
                clockPort.now()
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