package com.benhsoan.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(name = "role_id")
    private UUID roleId;

    private boolean active;

    private boolean locked;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at")
    private Instant createdAt;
}
