package com.benhsoan.persistence.jpaRepository.auth;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.benhsoan.persistence.entity.auth.UserEntity;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query(value = "SELECT u.* FROM users u JOIN roles r ON r.id = u.role_id WHERE r.name = 'DOCTOR' AND u.active = TRUE ORDER BY u.full_name", nativeQuery = true)
    List<UserEntity> findActiveDoctors();
}
