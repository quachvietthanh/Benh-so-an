package com.benhsoan.domain.auth;

import java.time.Instant;
import java.util.UUID;

import com.benhsoan.domain.auth.enums.ActionType;
import com.benhsoan.domain.auth.enums.ResourceType;
import com.benhsoan.domain.shared.Guard.Guard;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginLog {

    private UUID id;

    private UUID userId;

    private ActionType actionType;

    private ResourceType resourceType;

    private UUID resourceId;

    /**
     * JSON string
     */
    private String detail;

    private String ipAddress;

    private Instant createdAt;

    private LoginLog(
            UUID id,
            UUID userId,
            ActionType actionType,
            ResourceType resourceType,
            UUID resourceId,
            String detail,
            String ipAddress,
            Instant createdAt
    ) {
        this.id = Guard.require(id, "Log id");
        this.userId = Guard.require(userId, "User id");
        this.actionType = Guard.require(actionType, "Action type");
        this.resourceType = Guard.require(resourceType, "Resource type");
        this.createdAt = Guard.require(createdAt, "Created at");

        this.resourceId = resourceId;
        this.detail = detail;
        this.ipAddress = ipAddress;
    }

    public static LoginLog create(
            UUID userId,
            ActionType actionType,
            ResourceType resourceType,
            UUID resourceId,
            String detail,
            String ipAddress
    ) {
        return new LoginLog(
                UUID.randomUUID(),
                userId,
                actionType,
                resourceType,
                resourceId,
                detail,
                ipAddress,
                Instant.now()
        );
    }
}