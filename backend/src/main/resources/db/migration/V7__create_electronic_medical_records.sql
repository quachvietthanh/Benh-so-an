CREATE TABLE electronic_medical_records (
    id BINARY(16) NOT NULL,
    record_code VARCHAR(30) NOT NULL,
    patient_id BINARY(16) NOT NULL,
    doctor_id BINARY(16) NOT NULL,
    symptoms TEXT NOT NULL,
    examination_note TEXT,
    diagnosis VARCHAR(500) NOT NULL,
    treatment_plan TEXT,
    clinical_orders JSON,
    clinical_results JSON,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_electronic_medical_records PRIMARY KEY (id),
    CONSTRAINT uk_electronic_medical_records_code UNIQUE (record_code),
    CONSTRAINT fk_emr_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_emr_doctor FOREIGN KEY (doctor_id) REFERENCES users(id)
);

CREATE TABLE medical_record_attachments (
    id BINARY(16) NOT NULL,
    record_id BINARY(16) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    file_data LONGBLOB NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_medical_record_attachments PRIMARY KEY (id),
    CONSTRAINT fk_attachment_record FOREIGN KEY (record_id)
        REFERENCES electronic_medical_records(id) ON DELETE CASCADE
);

CREATE INDEX idx_emr_patient_created ON electronic_medical_records(patient_id, created_at);
CREATE INDEX idx_emr_doctor ON electronic_medical_records(doctor_id);
