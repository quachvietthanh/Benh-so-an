package com.benhsoan.domain.auth;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

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
@NoArgsConstructor(access=AccessLevel.PROTECTED)

public class Role {
    private UUID id;
    private String name;
    private String description;
    private boolean isSystem;
    private Instant createdAt;

    private Role(UUID id,  String name, String description, boolean isSystem, Instant createdAt ){
        this.id = Objects.requireNonNull(id);
        this.name = Guard.require(name, "Role name");
        this.description = description;
        this.isSystem = isSystem;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Role create(String name, String description, boolean isSystem){
        return new Role( UUID.randomUUID(), name, description, isSystem, Instant.now());
    }

    public void rename(String name) {
        if (isSystem) {
            throw new ValidationException("System role cannot be renamed.");
    }
    this.name = Guard.require(name, "Role name");
}

    public void changeDescription(String description){
        this.description = description;
    }

    public static Role restore(
        UUID id,
        String name,
        String description,
        boolean isSystem,
        Instant createdAt) {

    return new Role(id, name, description, isSystem, createdAt);
}

}
