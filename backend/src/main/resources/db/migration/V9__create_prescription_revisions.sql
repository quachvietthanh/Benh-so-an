CREATE TABLE prescription_revisions (
    id BINARY(16) PRIMARY KEY,
    prescription_id BINARY(16) NOT NULL,
    changed_by BINARY(16) NOT NULL,
    previous_items JSON NOT NULL,
    new_items JSON NOT NULL,
    change_reason VARCHAR(500) NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_revision_prescription FOREIGN KEY(prescription_id) REFERENCES prescriptions(id),
    CONSTRAINT fk_revision_user FOREIGN KEY(changed_by) REFERENCES users(id)
);
