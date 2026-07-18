package com.benhsoan.application.service.admin;

import com.benhsoan.domain.auth.User;
import com.benhsoan.port.inbound.admin.UpdateUserStatusUseCase;
import com.benhsoan.port.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatusService implements UpdateUserStatusUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public Map<String, Object> updateUserStatus(UUID userId, boolean locked, String adminUsername) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với ID: " + userId));

        if (locked && targetUser.getUsername().equals(adminUsername)) {
            log.warn("Admin '{}' attempted to lock themselves", adminUsername);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bạn không thể tự khóa tài khoản của chính mình");
        }

        if (locked) {
            targetUser.lock();
            log.info("User '{}' has been locked by admin '{}'", targetUser.getUsername(), adminUsername);
        } else {
            targetUser.unlock();
            log.info("User '{}' has been unlocked by admin '{}'", targetUser.getUsername(), adminUsername);
        }

        userRepository.save(targetUser);

        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.OK.value(),
                "message", locked
                        ? "Đã khóa tài khoản người dùng '" + targetUser.getUsername() + "'"
                        : "Đã mở khóa tài khoản người dùng '" + targetUser.getUsername() + "'",
                "userId", targetUser.getId().toString(),
                "username", targetUser.getUsername(),
                "locked", targetUser.isLocked()
        );
    }
}
