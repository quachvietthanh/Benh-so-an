ALTER TABLE medicines ADD COLUMN sale_price DECIMAL(15,2) NOT NULL DEFAULT 0;
UPDATE medicines SET sale_price=1000 WHERE name='Aspirin 81mg';
UPDATE medicines SET sale_price=2500 WHERE name='Ibuprofen 400mg';
UPDATE medicines SET sale_price=1000 WHERE name='Paracetamol 500mg';
UPDATE medicines SET sale_price=1500 WHERE name='Metformin 500mg';

CREATE TABLE payments (
 id BINARY(16) PRIMARY KEY, payment_code VARCHAR(30) NOT NULL UNIQUE,
 prescription_id BINARY(16) NOT NULL UNIQUE, patient_id BINARY(16) NOT NULL,
 exam_fee DECIMAL(15,2) NOT NULL, medicine_fee DECIMAL(15,2) NOT NULL,
 total_amount DECIMAL(15,2) NOT NULL, payment_method VARCHAR(30) NOT NULL,
 received_by BINARY(16) NOT NULL, paid_at TIMESTAMP NOT NULL,
 CONSTRAINT fk_payment_prescription FOREIGN KEY(prescription_id) REFERENCES prescriptions(id),
 CONSTRAINT fk_payment_patient FOREIGN KEY(patient_id) REFERENCES patients(id),
 CONSTRAINT fk_payment_user FOREIGN KEY(received_by) REFERENCES users(id)
);

CREATE TABLE invoices (
 id BINARY(16) PRIMARY KEY, invoice_code VARCHAR(30) NOT NULL UNIQUE,
 payment_id BINARY(16), patient_id BINARY(16) NOT NULL,
 original_invoice_id BINARY(16), invoice_type VARCHAR(20) NOT NULL,
 line_items JSON NOT NULL, total_amount DECIMAL(15,2) NOT NULL,
 adjustment_reason VARCHAR(500), issued_by BINARY(16) NOT NULL, issued_at TIMESTAMP NOT NULL,
 CONSTRAINT fk_invoice_payment FOREIGN KEY(payment_id) REFERENCES payments(id),
 CONSTRAINT fk_invoice_patient FOREIGN KEY(patient_id) REFERENCES patients(id),
 CONSTRAINT fk_invoice_original FOREIGN KEY(original_invoice_id) REFERENCES invoices(id),
 CONSTRAINT fk_invoice_user FOREIGN KEY(issued_by) REFERENCES users(id)
);
