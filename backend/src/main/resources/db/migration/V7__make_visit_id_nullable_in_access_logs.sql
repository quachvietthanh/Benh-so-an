-- =====================================================
-- V7__make_visit_id_nullable_in_access_logs.sql
-- Make visit_id nullable in medical_record_access_logs
-- to support VIEW_MEDICAL_HISTORY action (page view
-- without a specific visit).
-- =====================================================

ALTER TABLE medical_record_access_logs
    DROP FOREIGN KEY fk_medical_record_access_logs_visit,
    MODIFY visit_id BINARY(16) NULL;

-- Re-add FK but with ON DELETE SET NULL instead
ALTER TABLE medical_record_access_logs
    ADD CONSTRAINT fk_medical_record_access_logs_visit
        FOREIGN KEY (visit_id)
        REFERENCES visits(id)
        ON DELETE SET NULL;
