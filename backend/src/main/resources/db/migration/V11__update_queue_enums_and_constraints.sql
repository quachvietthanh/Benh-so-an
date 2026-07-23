-- =====================================================
-- V11__update_queue_enums_and_constraints.sql
-- Add SKIPPED status and APPOINTMENT priority
-- =====================================================

-- ===========================
-- 1. Drop old CHECK constraints
-- ===========================
ALTER TABLE medical_queue
    DROP CONSTRAINT ck_medical_queue_status;

ALTER TABLE medical_queue
    DROP CONSTRAINT ck_medical_queue_priority;

-- ===========================
-- 2. Re-create CHECK constraints with new values
-- ===========================
ALTER TABLE medical_queue
    ADD CONSTRAINT ck_medical_queue_status
        CHECK (status IN (
            'WAITING',
            'IN_PROGRESS',
            'WAITING_FOR_RESULT',
            'SKIPPED',
            'COMPLETED',
            'CANCELLED'
        ));

ALTER TABLE medical_queue
    ADD CONSTRAINT ck_medical_queue_priority
        CHECK (priority_level IN (
            'EMERGENCY',
            'APPOINTMENT',
            'REGULAR'
        ));
