-- ===========================
-- Permissions
-- ===========================

CREATE TABLE permissions (
    id BINARY(16) NOT NULL,
    feature_code VARCHAR(100) NOT NULL,
    action VARCHAR(30) NOT NULL,
    description TEXT,

    CONSTRAINT pk_permissions PRIMARY KEY (id)
);

-- ===========================
-- Role Permissions
-- ===========================

CREATE TABLE role_permissions (
    role_id BINARY(16) NOT NULL,
    permission_id BINARY(16) NOT NULL,

    CONSTRAINT pk_role_permissions
        PRIMARY KEY (role_id, permission_id),

    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_role_permissions_permission
    ON role_permissions(permission_id);