package com.benhsoan.port.outbound.repository;

import com.benhsoan.domain.auth.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {
    Optional<Role> findById(UUID id);
    Optional<Role> findByName(String name);
    List<Role> findAll();
    Role save(Role role);
    void deleteById(UUID id);
    boolean existsByName(String name);
}
