package com.benhsoan.port.outbound.repository;

import com.benhsoan.domain.auth.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User save(User user);
    void deleteById(UUID id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
