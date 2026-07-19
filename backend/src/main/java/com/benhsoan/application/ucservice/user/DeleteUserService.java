package com.benhsoan.application.ucservice.user;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.auth.exception.UserNotFoundException;
import com.benhsoan.port.inbound.user.DeleteUserUseCase;
import com.benhsoan.port.outbound.repository.crudRepository.auth.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepository userRepository;

    @Override
    public void delete(UUID id) {

        if (!userRepository.findById(id).isPresent()) {
            throw new UserNotFoundException();
        }

        userRepository.deleteById(id);
    }
}