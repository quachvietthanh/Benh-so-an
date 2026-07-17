package com.benhsoan.infrastructure.persistence.adapter;

import com.benhsoan.domain.auth.User;
import com.benhsoan.infrastructure.persistence.entity.UserEntity;
import com.benhsoan.infrastructure.persistence.repository.JpaUserRepository;
import com.benhsoan.port.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = jpaUserRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .passwordHash(entity.getPasswordHash())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .roleId(entity.getRoleId())
                .active(entity.isActive())
                .lastLoginAt(entity.getLastLoginAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .passwordHash(domain.getPasswordHash())
                .fullName(domain.getFullName())
                .email(domain.getEmail())
                .phone(domain.getPhone())
                .roleId(domain.getRoleId())
                .active(domain.isActive())
                .locked(!domain.isActive())
                .lastLoginAt(domain.getLastLoginAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
