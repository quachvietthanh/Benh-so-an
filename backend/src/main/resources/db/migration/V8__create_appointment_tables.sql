-- =====================================================
-- V8__create_appointment_tables.sql
-- Appointment Management Schema
-- =====================================================

-- ===========================
-- Appointments
-- ===========================

CREATE TABLE appointments (
    id BINARY(16) NOT NULL,

    appointment_code VARCHAR(30) NOT NULL,

    patient_id BINARY(16) NOT NULL,

    doctor_id BINARY(16) NOT NULL,

    start_time TIMESTAMP NOT NULL,

    end_time TIMESTAMP NOT NULL,

    status VARCHAR(30) NOT NULL,

    reason VARCHAR(500),

    cancel_reason VARCHAR(500),

    checked_in_at TIMESTAMP NULL,

    completed_at TIMESTAMP NULL,

    created_by BINARY(16) NOT NULL,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_appointments
        PRIMARY KEY (id),

    CONSTRAINT uk_appointments_code
        UNIQUE (appointment_code),

    CONSTRAINT fk_appointments_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id),

    CONSTRAINT fk_appointments_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES users(id),

    CONSTRAINT fk_appointments_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
);

-- ===========================
-- Appointment Queues
-- ===========================

CREATE TABLE appointment_queues (
    id BINARY(16) NOT NULL,

    appointment_id BINARY(16) NOT NULL,

    queue_number INT NOT NULL,

    status VARCHAR(30) NOT NULL,

    checked_in_at TIMESTAMP NULL,

    called_at TIMESTAMP NULL,

    started_at TIMESTAMP NULL,

    completed_at TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_appointment_queues
        PRIMARY KEY (id),

    CONSTRAINT uk_appointment_queues_appointment
        UNIQUE (appointment_id),

    CONSTRAINT fk_appointment_queues_appointment
        FOREIGN KEY (appointment_id)
        REFERENCES appointments(id)
        ON DELETE CASCADE
);

-- ===========================
-- Appointment Reminders
-- ===========================

CREATE TABLE appointment_reminders (
    id BINARY(16) NOT NULL,

    appointment_id BINARY(16) NOT NULL,

    channel VARCHAR(30) NOT NULL,

    remind_at TIMESTAMP NOT NULL,

    status VARCHAR(30) NOT NULL,

    sent_at TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_appointment_reminders
        PRIMARY KEY (id),

    CONSTRAINT fk_appointment_reminders_appointment
        FOREIGN KEY (appointment_id)
        REFERENCES appointments(id)
        ON DELETE CASCADE
);

-- =====================================================
-- Indexes
-- =====================================================

CREATE INDEX idx_appointments_patient
    ON appointments(patient_id);

CREATE INDEX idx_appointments_doctor
    ON appointments(doctor_id);

CREATE INDEX idx_appointments_created_by
    ON appointments(created_by);

CREATE INDEX idx_appointments_status
    ON appointments(status);

CREATE INDEX idx_appointments_start_time
    ON appointments(start_time);

CREATE INDEX idx_appointments_end_time
    ON appointments(end_time);

CREATE INDEX idx_appointment_queues_status
    ON appointment_queues(status);

CREATE INDEX idx_appointment_queues_queue_number
    ON appointment_queues(queue_number);

CREATE INDEX idx_appointment_reminders_appointment
    ON appointment_reminders(appointment_id);

CREATE INDEX idx_appointment_reminders_status
    ON appointment_reminders(status);

CREATE INDEX idx_appointment_reminders_remind_at
    ON appointment_reminders(remind_at);