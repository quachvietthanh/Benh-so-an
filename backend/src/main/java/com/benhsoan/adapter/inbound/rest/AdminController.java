package com.benhsoan.adapter.inbound.rest;

import com.benhsoan.port.inbound.admin.UpdateUserStatusUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UpdateUserStatusUseCase updateUserStatusUseCase;

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable UUID id,
            @RequestParam boolean locked,
            Authentication authentication) {

        String adminUsername = authentication.getName();
        Map<String, Object> result = updateUserStatusUseCase.updateUserStatus(id, locked, adminUsername);

        return ResponseEntity.ok(result);
    }
}
