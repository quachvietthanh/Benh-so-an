package com.benhsoan.application.ucservice.user;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.auth.Role;
import com.benhsoan.domain.auth.User;
import com.benhsoan.domain.auth.exception.RoleNotFoundException;
import com.benhsoan.domain.auth.exception.UserNotFoundException;
import com.benhsoan.dto.response.auth.UserResponse;
import com.benhsoan.port.inbound.user.ActivateUserUseCase;
import com.benhsoan.port.outbound.repository.RoleRepository;
import com.benhsoan.port.outbound.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivateUserService implements ActivateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserResponse activate(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.activate();

        User saved = userRepository.save(user);

        Role role = roleRepository.findById(saved.getRoleId())
                .orElseThrow(RoleNotFoundException::new);

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