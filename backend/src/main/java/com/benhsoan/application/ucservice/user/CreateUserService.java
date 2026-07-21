package com.benhsoan.application.ucservice.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.auth.Role;
import com.benhsoan.domain.auth.User;
import com.benhsoan.domain.auth.exception.EmailAlreadyExistsException;
import com.benhsoan.domain.auth.exception.RoleNotFoundException;
import com.benhsoan.domain.auth.exception.UserAlreadyExistsException;
import com.benhsoan.port.dto.command.user.CreateUserCommand;
import com.benhsoan.port.dto.result.UserResult;
import com.benhsoan.port.inbound.user.CreateUserUseCase;
import com.benhsoan.port.outbound.authSecurity.PasswordEncoderPort;
import com.benhsoan.port.outbound.repository.crudRepository.auth.RoleRepository;
import com.benhsoan.port.outbound.repository.crudRepository.auth.UserRepository;
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
    private final UserResultMapper userResultMapper;

    @Override
    public UserResult createUser(
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
        
        return userResultMapper.toResult(saved, role );
    }
}