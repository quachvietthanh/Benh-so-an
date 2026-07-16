package com.benhsoan.port.outbound.repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.benhsoan.domain.auth.Role;

public interface RoleRepository extends BaseRepository<Role, UUID> {

    Optional<Role> findByName(String name);

    List<Role> findAll();

    boolean existsByName(String name);
}