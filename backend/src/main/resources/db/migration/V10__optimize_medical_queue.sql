-- =====================================================
-- V10__optimize_medical_queue.sql
-- Concurrency-safe & Performance Optimization
-- =====================================================

-- ===========================
-- 1. UNIQUE constraint: đảm bảo không trùng số thứ tự
--    trong cùng một ngày tại một phòng khám
-- ===========================
-- MySQL không hỗ trợ FUNCTION-based UNIQUE index trực tiếp,
-- dùng GENERATED COLUMN để lưu ngày dạng DATE + UNIQUE

ALTER TABLE medical_queue
    ADD COLUMN queue_date DATE GENERATED ALWAYS AS (CAST(created_at AS DATE)) STORED;

ALTER TABLE medical_queue
    ADD CONSTRAINT uk_room_queue_date
        UNIQUE (room_number, queue_number, queue_date);

-- ===========================
-- 2. Composite Index tối ưu sorting queue
--    (room_number, status, priority_level, checked_in_at)
--    Phục vụ query: "lấy danh sách queue trong phòng X,
--    sắp xếp EMERGENCY lên trước, theo thời gian check-in"
-- ===========================

CREATE INDEX idx_queue_sorting
    ON medical_queue(room_number, status, priority_level, checked_in_at);

-- ===========================
-- 3. Drop old indexes that are now covered by new ones
-- ===========================

DROP INDEX idx_medical_queue_room_status ON medical_queue;
DROP INDEX idx_medical_queue_priority_status ON medical_queue;
