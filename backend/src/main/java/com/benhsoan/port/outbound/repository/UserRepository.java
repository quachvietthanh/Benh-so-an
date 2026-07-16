package com.benhsoan.port.outbound.repository;

import java.util.Optional;
import java.util.UUID;

import com.benhsoan.domain.auth.User;

public interface UserRepository extends BaseRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}