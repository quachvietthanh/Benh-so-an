package com.benhsoan.port.outbound.repository.crudRepository.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.benhsoan.domain.auth.Role;
import com.benhsoan.port.outbound.repository.BaseRepository;

public interface RoleRepository extends BaseRepository<Role, UUID> {

    Optional<Role> findByName(String name);

    List<Role> findAll();

    boolean existsByName(String name);
}