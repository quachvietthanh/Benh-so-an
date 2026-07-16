-- =====================================================
-- V3__seed_roles.sql
-- Seed default system roles
-- =====================================================

INSERT INTO roles (
    id,
    name,
    description,
    is_system,
    created_at
)
VALUES
(
    UUID_TO_BIN('11111111-1111-1111-1111-111111111111'),
    'ADMIN',
    'Quản trị hệ thống',
    TRUE,
    CURRENT_TIMESTAMP
),
(
    UUID_TO_BIN('22222222-2222-2222-2222-222222222222'),
    'DOCTOR',
    'Bác sĩ',
    TRUE,
    CURRENT_TIMESTAMP
),
(
    UUID_TO_BIN('33333333-3333-3333-3333-333333333333'),
    'NURSE',
    'Điều dưỡng',
    TRUE,
    CURRENT_TIMESTAMP
),
(
    UUID_TO_BIN('44444444-4444-4444-4444-444444444444'),
    'RECEPTIONIST',
    'Lễ tân',
    TRUE,
    CURRENT_TIMESTAMP
),
(
    UUID_TO_BIN('55555555-5555-5555-5555-555555555555'),
    'PHARMACIST',
    'Dược sĩ',
    TRUE,
    CURRENT_TIMESTAMP
);