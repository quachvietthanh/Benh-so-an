package com.benhsoan.domain.auth;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.benhsoan.domain.auth.enums.Permission;
import com.benhsoan.domain.shared.Guard.Guard;
import com.benhsoan.domain.shared.exception.ValidationException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    private UUID id;

    private String name;

    private String description;

    private boolean system;

    private Instant createdAt;

    private Instant updatedAt;

    private final Set<Permission> permissions = new HashSet<>();

    private Role(
            UUID id,
            String name,
            String description,
            boolean system,
            Instant createdAt,
            Instant updatedAt,
            Set<Permission> permissions
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Guard.require(name, "Role name");
        this.description = description;
        this.system = system;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = updatedAt;

        if (permissions != null) {
            this.permissions.addAll(permissions);
        }
    }

    public static Role create(
            String name,
            String description,
            boolean system,
            Set<Permission> permissions
    ) {
        Instant now = Instant.now();

        return new Role(
                UUID.randomUUID(),
                name,
                description,
                system,
                now,
                now,
                permissions
        );
    }

    public static Role restore(
            UUID id,
            String name,
            String description,
            boolean system,
            Instant createdAt,
            Instant updatedAt,
            Set<Permission> permissions
    ) {
        return new Role(
                id,
                name,
                description,
                system,
                createdAt,
                updatedAt,
                permissions
        );
    }

    public void rename(String name) {
        if (system) {
            throw new ValidationException("System role cannot be renamed.");
        }

        this.name = Guard.require(name, "Role name");
        this.updatedAt = Instant.now();
    }

    public void changeDescription(String description) {
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void addPermission(Permission permission) {
        Objects.requireNonNull(permission);

        if (permissions.add(permission)) {
            updatedAt = Instant.now();
        }
    }

    public void removePermission(Permission permission) {
        if (permissions.remove(permission)) {
            updatedAt = Instant.now();
        }
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public boolean hasAnyPermission(Permission... permissions) {
        return Arrays.stream(permissions)
                .anyMatch(this.permissions::contains);
    }

    public boolean hasAllPermissions(Permission... permissions) {
        return this.permissions.containsAll(Arrays.asList(permissions));
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }
}