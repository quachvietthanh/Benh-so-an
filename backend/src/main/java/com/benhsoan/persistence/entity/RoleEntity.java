package com.benhsoan.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity{
    @Id
    @Column(name="id", nullable=false,columnDefinition="BINARY(16)")
    private  UUID id;

    @Column(name="name", unique = true, nullable=false, length=50)
    private String name;

    @Column(name="description", length=255)
    private String description;

    @Column(name="is_system", nullable = false)
    private boolean isSystem;

    @Column(name="created_at", nullable = false) 
    private Instant createdAt;
}