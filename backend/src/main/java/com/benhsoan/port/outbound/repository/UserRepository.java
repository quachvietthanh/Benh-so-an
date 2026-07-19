package com.benhsoan.port.outbound.repository;

import com.benhsoan.domain.auth.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    User save(User user);
    void deleteById(UUID id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
