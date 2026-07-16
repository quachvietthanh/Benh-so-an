-- ==========================================
-- Cơ sở dữ liệu: Bệnh số án
-- Hệ thống chuyển đổi số cơ sở khám chữa bệnh
-- ==========================================

CREATE DATABASE IF NOT EXISTS benhsoan_db;
USE benhsoan_db;

-- ==========================================
-- PHẦN 1: CẤU TRÚC CƠ BẢN
-- ==========================================

-- Bảng người dùng
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Bảng bệnh nhân
CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    patient_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    phone_number VARCHAR(15),
    email VARCHAR(100),
    address TEXT,
    identity_number VARCHAR(20),
    health_insurance_code VARCHAR(20),
    blood_type VARCHAR(5),
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(15),
    medical_history TEXT,
    allergies TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng hồ sơ bệnh án
CREATE TABLE IF NOT EXISTS medical_records (
    id BIGSERIAL PRIMARY KEY,
    record_code VARCHAR(20) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT,
    diagnosis TEXT,
    symptoms TEXT,
    treatment TEXT,
    prescription TEXT,
    notes TEXT,
    status VARCHAR(20) DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ==========================================
-- PHẦN 2: CẤU TRÚC PHÂN QUYỀN MỞ RỘNG
-- ==========================================

-- Bảng danh sách quyền chi tiết
CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng vai trò
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_system BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng quan hệ vai trò - quyền
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Bảng phân cấp vai trò (role hierarchy)
CREATE TABLE IF NOT EXISTS role_hierarchy (
    parent_role_id BIGINT NOT NULL,
    child_role_id BIGINT NOT NULL,
    PRIMARY KEY (parent_role_id, child_role_id),
    FOREIGN KEY (parent_role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (child_role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Bảng phiên đăng nhập
CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    refresh_token VARCHAR(500),
    ip_address VARCHAR(45),
    user_agent TEXT,
    login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_active_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Bảng nhật ký đăng nhập
CREATE TABLE IF NOT EXISTS login_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    ip_address VARCHAR(45),
    user_agent TEXT,
    status VARCHAR(50) NOT NULL,
    failure_reason VARCHAR(255),
    login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- PHẦN 3: DỮ LIỆU MẪU (PERMISSIONS + ROLES)
-- ==========================================

-- Insert permissions
INSERT INTO permissions (code, name, description, category) VALUES
('USER_CREATE', 'Tạo người dùng', 'Quyền tạo tài khoản người dùng mới', 'USER'),
('USER_READ', 'Xem người dùng', 'Quyền xem thông tin người dùng', 'USER'),
('USER_UPDATE', 'Cập nhật người dùng', 'Quyền cập nhật thông tin người dùng', 'USER'),
('USER_DELETE', 'Xóa người dùng', 'Quyền xóa tài khoản người dùng', 'USER'),
('USER_ASSIGN_ROLE', 'Gán vai trò', 'Quyền gán vai trò cho người dùng', 'USER'),

('PATIENT_CREATE', 'Tạo bệnh nhân', 'Quyền tạo hồ sơ bệnh nhân mới', 'PATIENT'),
('PATIENT_READ', 'Xem bệnh nhân', 'Quyền xem thông tin bệnh nhân', 'PATIENT'),
('PATIENT_UPDATE', 'Cập nhật bệnh nhân', 'Quyền cập nhật thông tin bệnh nhân', 'PATIENT'),
('PATIENT_DELETE', 'Xóa bệnh nhân', 'Quyền xóa hồ sơ bệnh nhân', 'PATIENT'),

('RECORD_CREATE', 'Tạo hồ sơ bệnh án', 'Quyền tạo hồ sơ bệnh án mới', 'RECORD'),
('RECORD_READ', 'Xem hồ sơ bệnh án', 'Quyền xem hồ sơ bệnh án', 'RECORD'),
('RECORD_UPDATE', 'Cập nhật hồ sơ bệnh án', 'Quyền chỉnh sửa hồ sơ bệnh án', 'RECORD'),
('RECORD_DELETE', 'Xóa hồ sơ bệnh án', 'Quyền xóa hồ sơ bệnh án', 'RECORD'),
('RECORD_UPDATE_STATUS', 'Cập nhật trạng thái', 'Quyền cập nhật trạng thái hồ sơ', 'RECORD'),

('PRESCRIPTION_CREATE', 'Tạo đơn thuốc', 'Quyền kê đơn thuốc', 'PRESCRIPTION'),
('PRESCRIPTION_READ', 'Xem đơn thuốc', 'Quyền xem đơn thuốc', 'PRESCRIPTION'),
('PRESCRIPTION_UPDATE', 'Cập nhật đơn thuốc', 'Quyền chỉnh sửa đơn thuốc', 'PRESCRIPTION'),
('PRESCRIPTION_DELETE', 'Xóa đơn thuốc', 'Quyền xóa đơn thuốc', 'PRESCRIPTION'),
('PRESCRIPTION_UPDATE_STATUS', 'Duyệt đơn thuốc', 'Quyền duyệt/cập nhật trạng thái đơn thuốc', 'PRESCRIPTION'),

('APPOINTMENT_CREATE', 'Tạo lịch hẹn', 'Quyền tạo lịch hẹn mới', 'APPOINTMENT'),
('APPOINTMENT_READ', 'Xem lịch hẹn', 'Quyền xem lịch hẹn', 'APPOINTMENT'),
('APPOINTMENT_UPDATE', 'Cập nhật lịch hẹn', 'Quyền chỉnh sửa lịch hẹn', 'APPOINTMENT'),
('APPOINTMENT_DELETE', 'Xóa lịch hẹn', 'Quyền hủy/xóa lịch hẹn', 'APPOINTMENT'),

('VITAL_SIGN_CREATE', 'Ghi dấu hiệu sinh tồn', 'Quyền ghi chỉ số sinh tồn', 'VITAL_SIGN'),
('VITAL_SIGN_READ', 'Xem dấu hiệu sinh tồn', 'Quyền xem chỉ số sinh tồn', 'VITAL_SIGN'),
('VITAL_SIGN_UPDATE', 'Cập nhật dấu hiệu sinh tồn', 'Quyền chỉnh sửa chỉ số sinh tồn', 'VITAL_SIGN'),

('DIAGNOSIS_CREATE', 'Tạo chẩn đoán', 'Quyền thêm chẩn đoán mới', 'DIAGNOSIS'),
('DIAGNOSIS_READ', 'Xem chẩn đoán', 'Quyền xem chẩn đoán', 'DIAGNOSIS'),
('DIAGNOSIS_UPDATE', 'Cập nhật chẩn đoán', 'Quyền chỉnh sửa chẩn đoán', 'DIAGNOSIS'),

('PHARMACY_CREATE', 'Nhập kho', 'Quyền nhập thuốc vào kho', 'PHARMACY'),
('PHARMACY_READ', 'Xem kho thuốc', 'Quyền xem tồn kho thuốc', 'PHARMACY'),
('PHARMACY_UPDATE', 'Cập nhật kho', 'Quyền điều chỉnh tồn kho', 'PHARMACY'),
('PHARMACY_DELETE', 'Xóa khỏi kho', 'Quyền xóa thuốc khỏi kho', 'PHARMACY'),

('INVOICE_CREATE', 'Tạo hóa đơn', 'Quyền tạo hóa đơn thanh toán', 'INVOICE'),
('INVOICE_READ', 'Xem hóa đơn', 'Quyền xem hóa đơn', 'INVOICE'),
('INVOICE_UPDATE', 'Cập nhật hóa đơn', 'Quyền chỉnh sửa hóa đơn', 'INVOICE'),
('INVOICE_DELETE', 'Xóa hóa đơn', 'Quyền hủy hóa đơn', 'INVOICE'),

('AUDIT_READ', 'Xem nhật ký', 'Quyền xem nhật ký hệ thống', 'AUDIT'),

('ROLE_READ', 'Xem vai trò', 'Quyền xem danh sách vai trò', 'ROLE'),
('ROLE_CREATE', 'Tạo vai trò', 'Quyền tạo vai trò mới', 'ROLE'),
('ROLE_UPDATE', 'Cập nhật vai trò', 'Quyền chỉnh sửa vai trò', 'ROLE'),
('ROLE_DELETE', 'Xóa vai trò', 'Quyền xóa vai trò', 'ROLE'),
('PERMISSION_READ', 'Xem quyền', 'Quyền xem danh sách quyền', 'PERMISSION');

-- Insert roles
INSERT INTO roles (name, description, is_system) VALUES
('ADMIN', 'Quản trị viên hệ thống', TRUE),
('DOCTOR', 'Bác sĩ', TRUE),
('NURSE', 'Y tá', TRUE),
('RECEPTIONIST', 'Lễ tân', TRUE),
('PHARMACIST', 'Dược sĩ', TRUE);

-- Gán quyền cho ADMIN (tất cả quyền)
INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'ADMIN'), id FROM permissions;

-- Gán quyền cho DOCTOR
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DOCTOR'
AND p.code IN (
    'PATIENT_CREATE', 'PATIENT_READ', 'PATIENT_UPDATE',
    'RECORD_CREATE', 'RECORD_READ', 'RECORD_UPDATE', 'RECORD_UPDATE_STATUS',
    'PRESCRIPTION_CREATE', 'PRESCRIPTION_READ', 'PRESCRIPTION_UPDATE', 'PRESCRIPTION_DELETE',
    'APPOINTMENT_CREATE', 'APPOINTMENT_READ', 'APPOINTMENT_UPDATE',
    'VITAL_SIGN_CREATE', 'VITAL_SIGN_READ', 'VITAL_SIGN_UPDATE',
    'DIAGNOSIS_CREATE', 'DIAGNOSIS_READ', 'DIAGNOSIS_UPDATE'
);

-- Gán quyền cho NURSE
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'NURSE'
AND p.code IN (
    'PATIENT_READ',
    'RECORD_READ', 'RECORD_UPDATE_STATUS',
    'APPOINTMENT_READ',
    'VITAL_SIGN_CREATE', 'VITAL_SIGN_READ', 'VITAL_SIGN_UPDATE'
);

-- Gán quyền cho RECEPTIONIST
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'RECEPTIONIST'
AND p.code IN (
    'PATIENT_CREATE', 'PATIENT_READ', 'PATIENT_UPDATE',
    'APPOINTMENT_CREATE', 'APPOINTMENT_READ', 'APPOINTMENT_UPDATE', 'APPOINTMENT_DELETE',
    'INVOICE_CREATE', 'INVOICE_READ', 'INVOICE_UPDATE'
);

-- Gán quyền cho PHARMACIST
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'PHARMACIST'
AND p.code IN (
    'PRESCRIPTION_READ', 'PRESCRIPTION_UPDATE_STATUS',
    'PHARMACY_CREATE', 'PHARMACY_READ', 'PHARMACY_UPDATE'
);

-- Indexes
CREATE INDEX idx_patients_code ON patients(patient_code);
CREATE INDEX idx_patients_name ON patients(full_name);
CREATE INDEX idx_medical_records_patient ON medical_records(patient_id);
CREATE INDEX idx_medical_records_doctor ON medical_records(doctor_id);
CREATE INDEX idx_medical_records_status ON medical_records(status);
CREATE INDEX idx_user_sessions_user ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_token ON user_sessions(token);
CREATE INDEX idx_login_logs_user ON login_logs(user_id);
CREATE INDEX idx_login_logs_username ON login_logs(username);
CREATE INDEX idx_login_logs_login_at ON login_logs(login_at);
CREATE INDEX idx_role_permissions_role ON role_permissions(role_id);
CREATE INDEX idx_role_hierarchy_parent ON role_hierarchy(parent_role_id);
