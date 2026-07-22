CREATE TABLE appointments (
    id BINARY(16) NOT NULL,
    appointment_code VARCHAR(20) NOT NULL,
    patient_id BINARY(16) NOT NULL,
    doctor_id BINARY(16) NOT NULL,
    department VARCHAR(100) NOT NULL,
    appointment_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(255),
    cancel_reason VARCHAR(500),
    checked_in_at TIMESTAMP NULL,
    reminder_sent_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_appointments PRIMARY KEY (id),
    CONSTRAINT uk_appointments_code UNIQUE (appointment_code),
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES users(id)
);

CREATE INDEX idx_appointments_time ON appointments(appointment_at);
CREATE INDEX idx_appointments_doctor_time ON appointments(doctor_id, appointment_at);
CREATE INDEX idx_appointments_queue ON appointments(status, checked_in_at);
