package com.benhsoan.adapter.inbound.rest;

import com.benhsoan.domain.auth.User;
import com.benhsoan.port.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable UUID id,
            @RequestParam boolean locked,
            Authentication authentication) {

        String adminUsername = authentication.getName();

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với ID: " + id));

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

        return ResponseEntity.ok(Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.OK.value(),
                "message", locked
                        ? "Đã khóa tài khoản người dùng '" + targetUser.getUsername() + "'"
                        : "Đã mở khóa tài khoản người dùng '" + targetUser.getUsername() + "'",
                "userId", targetUser.getId().toString(),
                "username", targetUser.getUsername(),
                "locked", targetUser.isLocked()
        ));
    }
}
