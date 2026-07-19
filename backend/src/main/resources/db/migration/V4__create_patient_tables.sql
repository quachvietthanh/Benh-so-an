-- =====================================================
-- V4__create_patient_tables.sql
-- Patient Management Schema
-- =====================================================

-- ===========================
-- Patients
-- ===========================

CREATE TABLE patients (
    id BINARY(16) NOT NULL,
    patient_code VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    identity_number VARCHAR(20),
    insurance_number VARCHAR(30),
    blood_type VARCHAR(10),
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BINARY(16) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_patients PRIMARY KEY (id),

    CONSTRAINT uk_patients_code
        UNIQUE (patient_code),

    CONSTRAINT uk_patients_identity
        UNIQUE (identity_number),

    CONSTRAINT fk_patients_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
);

-- ===========================
-- Visits
-- ===========================

CREATE TABLE visits (
    id BINARY(16) NOT NULL,
    patient_id BINARY(16) NOT NULL,
    doctor_id BINARY(16) NOT NULL,
    department_id BINARY(16) NOT NULL,
    visit_code VARCHAR(20) NOT NULL,
    visit_type VARCHAR(20) NOT NULL,
    visit_status VARCHAR(20) NOT NULL,
    visit_at TIMESTAMP NOT NULL,
    reason VARCHAR(255),
    note TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_visits PRIMARY KEY (id),

    CONSTRAINT uk_visits_code
        UNIQUE (visit_code),

    CONSTRAINT fk_visits_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id),

    CONSTRAINT fk_visits_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES users(id)
);

-- ===========================
-- Patient Change Logs
-- ===========================

CREATE TABLE patient_change_logs (
    id BINARY(16) NOT NULL,
    patient_id BINARY(16) NOT NULL,
    changed_by BINARY(16) NOT NULL,
    action VARCHAR(20) NOT NULL,
    change_detail JSON NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_patient_change_logs PRIMARY KEY (id),

    CONSTRAINT fk_patient_change_logs_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_patient_change_logs_user
        FOREIGN KEY (changed_by)
        REFERENCES users(id)
);

-- ===========================
-- Medical Record Access Logs
-- ===========================

CREATE TABLE medical_record_access_logs (
    id BINARY(16) NOT NULL,
    patient_id BINARY(16) NOT NULL,
    visit_id BINARY(16) NOT NULL,
    accessed_by BINARY(16) NOT NULL,
    action VARCHAR(20) NOT NULL,
    ip_address VARCHAR(45),
    accessed_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_medical_record_access_logs PRIMARY KEY (id),

    CONSTRAINT fk_medical_record_access_logs_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_medical_record_access_logs_visit
        FOREIGN KEY (visit_id)
        REFERENCES visits(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_medical_record_access_logs_user
        FOREIGN KEY (accessed_by)
        REFERENCES users(id)
);

-- =====================================================
-- Indexes
-- =====================================================

CREATE INDEX idx_patients_created_by
    ON patients(created_by);

CREATE INDEX idx_patients_full_name
    ON patients(full_name);

CREATE INDEX idx_patients_active
    ON patients(active);

CREATE INDEX idx_visits_patient
    ON visits(patient_id);

CREATE INDEX idx_visits_doctor
    ON visits(doctor_id);

CREATE INDEX idx_visits_visit_at
    ON visits(visit_at);

CREATE INDEX idx_visits_status
    ON visits(visit_status);

CREATE INDEX idx_patient_change_logs_patient
    ON patient_change_logs(patient_id);

CREATE INDEX idx_patient_change_logs_created_at
    ON patient_change_logs(created_at);

CREATE INDEX idx_medical_record_access_logs_patient
    ON medical_record_access_logs(patient_id);

CREATE INDEX idx_medical_record_access_logs_visit
    ON medical_record_access_logs(visit_id);

CREATE INDEX idx_medical_record_access_logs_accessed_at
    ON medical_record_access_logs(accessed_at);