CREATE TABLE emr_audit_logs (
 id BINARY(16) PRIMARY KEY, record_id BINARY(16) NOT NULL,
 patient_id BINARY(16) NOT NULL, user_id BINARY(16) NOT NULL,
 action VARCHAR(30) NOT NULL, accessed_at TIMESTAMP NOT NULL,
 CONSTRAINT fk_emr_audit_record FOREIGN KEY(record_id) REFERENCES electronic_medical_records(id),
 CONSTRAINT fk_emr_audit_patient FOREIGN KEY(patient_id) REFERENCES patients(id),
 CONSTRAINT fk_emr_audit_user FOREIGN KEY(user_id) REFERENCES users(id)
);
CREATE INDEX idx_emr_audit_time ON emr_audit_logs(accessed_at);
