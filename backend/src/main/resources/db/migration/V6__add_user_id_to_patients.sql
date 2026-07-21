-- =====================================================
-- V6__add_user_id_to_patients.sql
-- Add user_id column to link patients to user accounts
-- for patient self-service (view own medical history)
-- =====================================================

ALTER TABLE patients
    ADD COLUMN user_id BINARY(16) NULL,
    ADD CONSTRAINT uk_patients_user_id
        UNIQUE (user_id),
    ADD CONSTRAINT fk_patients_user
        FOREIGN KEY (user_id)
        REFERENCES users(id);

CREATE INDEX idx_patients_user_id
    ON patients(user_id);
