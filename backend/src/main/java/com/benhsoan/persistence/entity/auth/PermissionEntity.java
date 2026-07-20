package com.benhsoan.persistence.entity.auth;

import java.util.UUID;

import com.benhsoan.domain.auth.enums.PermissionAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionEntity {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "feature_code", nullable = false, length = 100)
    private String featureCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 30)
    private PermissionAction action;

    @Column(name = "description")
    private String description;
}