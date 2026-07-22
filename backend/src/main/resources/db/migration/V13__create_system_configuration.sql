CREATE TABLE clinic_services (
 id BINARY(16) PRIMARY KEY, service_code VARCHAR(30) NOT NULL,
 name VARCHAR(150) NOT NULL, price DECIMAL(15,2) NOT NULL,
 effective_from DATE NOT NULL, active BOOLEAN NOT NULL DEFAULT TRUE,
 created_at TIMESTAMP NOT NULL,
 CONSTRAINT uk_service_version UNIQUE(service_code,effective_from)
);
CREATE TABLE clinic_configuration (
 id INT PRIMARY KEY, clinic_name VARCHAR(200) NOT NULL,
 address VARCHAR(255), phone VARCHAR(30), opening_time TIME NOT NULL,
 closing_time TIME NOT NULL, examination_rooms JSON NOT NULL,
 updated_at TIMESTAMP NOT NULL
);
INSERT INTO clinic_configuration VALUES(1,'Bệnh Án Số','',NULL,'07:30:00','17:00:00',JSON_ARRAY('Phòng khám 1'),CURRENT_TIMESTAMP);
INSERT INTO clinic_services VALUES
(UUID_TO_BIN('cccccccc-0000-0000-0000-000000000001'),'KHAM-TQ','Khám tổng quát',150000,'2026-01-01',TRUE,CURRENT_TIMESTAMP),
(UUID_TO_BIN('cccccccc-0000-0000-0000-000000000002'),'XN-MAU','Xét nghiệm máu cơ bản',220000,'2026-01-01',TRUE,CURRENT_TIMESTAMP);
