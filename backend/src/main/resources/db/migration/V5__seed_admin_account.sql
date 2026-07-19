-- =====================================================
-- V5__seed_admin_account.sql
-- Seed default administrator account
-- =====================================================

-- Admin user
INSERT INTO users (
    id,
    username,
    password_hash,
    full_name,
    email,
    phone,
    role_id,
    active,
    last_login_at,
    created_at
)
VALUES (
    UUID_TO_BIN('22222222-2222-2222-2222-222222222222'),
    'admin',
    '$2a$10$OY5a1YZ/5Iaz2PcEKjfOveEyy3FVXm7ei9OxTW6jPMyap/Hlk.5sK',
    'System Administrator',
    'admin@benhsoan.com',
    NULL,
    UUID_TO_BIN('11111111-1111-1111-1111-111111111111'),
    TRUE,
    NULL,
    CURRENT_TIMESTAMP
);