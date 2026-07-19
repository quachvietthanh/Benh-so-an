package com.benhsoan.application.ucservice.user;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.auth.User;
import com.benhsoan.domain.auth.exception.UserNotFoundException;
import com.benhsoan.port.inbound.admin.UpdateUserStatusUseCase;
import com.benhsoan.port.outbound.repository.crudRepository.auth.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateUserStatusService implements UpdateUserStatusUseCase {

    private final UserRepository userRepository;

    @Override
    public Map<String, Object> updateUserStatus(UUID userId, boolean locked, String adminUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (locked) {
            user.lock();
        } else {
            user.unlock();
        }

        userRepository.save(user);

        return Map.of(
                "userId", user.getId().toString(),
                "username", user.getUsername(),
                "locked", user.isLocked(),
                "updatedBy", adminUsername,
                "updatedAt", Instant.now().toString()
        );
    }
}
