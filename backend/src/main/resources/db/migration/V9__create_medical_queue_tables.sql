-- =====================================================
-- V9__create_medical_queue_tables.sql
-- Queue Management Schema
-- =====================================================

-- ===========================
-- Medical Queue
-- ===========================
-- Trạng thái: WAITING -> IN_PROGRESS -> WAITING_FOR_RESULT -> COMPLETED
--                                             |                  |
--                                             +-> CANCELLED <---+
--                             WAITING -----------> CANCELLED

CREATE TABLE medical_queue (
    id BINARY(16) NOT NULL,

    patient_id BINARY(16) NOT NULL,

    doctor_id BINARY(16),

    room_number VARCHAR(10),

    queue_number INT NOT NULL,

    status VARCHAR(30) NOT NULL,

    priority_level VARCHAR(20) NOT NULL DEFAULT 'REGULAR',

    notes TEXT,

    checked_in_at TIMESTAMP NULL,

    called_at TIMESTAMP NULL,

    started_at TIMESTAMP NULL,

    waiting_for_result_at TIMESTAMP NULL,

    completed_at TIMESTAMP NULL,

    cancelled_at TIMESTAMP NULL,

    cancel_reason VARCHAR(500),

    created_by BINARY(16) NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_medical_queue
        PRIMARY KEY (id),

    CONSTRAINT fk_medical_queue_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id),

    CONSTRAINT fk_medical_queue_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES users(id),

    CONSTRAINT fk_medical_queue_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT ck_medical_queue_priority
        CHECK (priority_level IN ('EMERGENCY', 'REGULAR')),

    CONSTRAINT ck_medical_queue_status
        CHECK (status IN (
            'WAITING',
            'IN_PROGRESS',
            'WAITING_FOR_RESULT',
            'COMPLETED',
            'CANCELLED'
        ))
);

-- =====================================================
-- Indexes
-- =====================================================

CREATE INDEX idx_medical_queue_created_at
    ON medical_queue(created_at);

CREATE INDEX idx_medical_queue_status
    ON medical_queue(status);

CREATE INDEX idx_medical_queue_patient
    ON medical_queue(patient_id);

CREATE INDEX idx_medical_queue_doctor
    ON medical_queue(doctor_id);

CREATE INDEX idx_medical_queue_created_by
    ON medical_queue(created_by);

CREATE INDEX idx_medical_queue_room_status
    ON medical_queue(room_number, status);

CREATE INDEX idx_medical_queue_priority_status
    ON medical_queue(priority_level, status);
