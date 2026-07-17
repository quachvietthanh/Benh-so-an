# Ma trận phân quyền (Permission Matrix)

## 5 vai trò (Roles)

| Role | Mô tả |
|---|---|
| `ADMIN` | Quản trị viên hệ thống — toàn quyền |
| `DOCTOR` | Bác sĩ — khám, kê đơn, chẩn đoán |
| `NURSE` | Y tá — đo dấu hiệu sinh tồn, hỗ trợ |
| `RECEPTIONIST` | Lễ tân — tiếp nhận bệnh nhân, đặt lịch, thanh toán |
| `PHARMACIST` | Dược sĩ — quản lý thuốc, duyệt đơn |

## 35 quyền (Permissions)

### 👤 Người dùng (User)

| Permission | ADMIN | DOCTOR | NURSE | RECEPTIONIST | PHARMACIST |
|---|---|---|---|---|---|
| `USER_CREATE` | ✅ | — | — | — | — |
| `USER_READ` | ✅ | — | — | — | — |
| `USER_UPDATE` | ✅ | — | — | — | — |
| `USER_DELETE` | ✅ | — | — | — | — |
| `USER_ASSIGN_ROLE` | ✅ | — | — | — | — |

### 🏥 Bệnh nhân (Patient)

| Permission | ADMIN | DOCTOR | NURSE | RECEPTIONIST | PHARMACIST |
|---|---|---|---|---|---|
| `PATIENT_CREATE` | ✅ | ✅ | — | ✅ | — |
| `PATIENT_READ` | ✅ | ✅ | ✅ | ✅ | — |
| `PATIENT_UPDATE` | ✅ | ✅ | — | ✅ | — |
| `PATIENT_DELETE` | ✅ | — | — | — | — |

### 📋 Hồ sơ bệnh án (Medical Record)

| Permission | ADMIN | DOCTOR | NURSE | RECEPTIONIST | PHARMACIST |
|---|---|---|---|---|---|
| `RECORD_CREATE` | ✅ | ✅ | — | — | — |
| `RECORD_READ` | ✅ | ✅ | ✅ | — | — |
| `RECORD_UPDATE` | ✅ | ✅ | — | — | — |
| `RECORD_DELETE` | ✅ | — | — | — | — |
| `RECORD_UPDATE_STATUS` | ✅ | ✅ | ✅ | — | — |

### 💊 Đơn thuốc (Prescription)

| Permission | ADMIN | DOCTOR | NURSE | RECEPTIONIST | PHARMACIST |
|---|---|---|---|---|---|
| `PRESCRIPTION_CREATE` | ✅ | ✅ | — | — | — |
| `PRESCRIPTION_READ` | ✅ | ✅ | — | — | ✅ |
| `PRESCRIPTION_UPDATE` | ✅ | ✅ | — | — | — |
| `PRESCRIPTION_DELETE` | ✅ | ✅ | — | — | — |
| `PRESCRIPTION_UPDATE_STATUS` | ✅ | — | — | — | ✅ |

### 📅 Lịch hẹn (Appointment)

| Permission | ADMIN | DOCTOR | NURSE | RECEPTIONIST | PHARMACIST |
|---|---|---|---|---|---|
| `APPOINTMENT_CREATE` | ✅ | ✅ | — | ✅ | — |
| `APPOINTMENT_READ` | ✅ | ✅ | ✅ | ✅ | — |
| `APPOINTMENT_UPDATE` | ✅ | ✅ | — | ✅ | — |
| `APPOINTMENT_DELETE` | ✅ | — | — | ✅ | — |

### ❤️ Dấu hiệu sinh tồn (Vital Sign)

| Permission | ADMIN | DOCTOR | NURSE | RECEPTIONIST | PHARMACIST |
|---|---|---|---|---|---|
| `VITAL_SIGN_CREATE` | ✅ | ✅ | ✅ | — | — |
| `VITAL_SIGN_READ` | ✅ | ✅ | ✅ | — | — |
| `VITAL_SIGN_UPDATE` | ✅ | ✅ | ✅ | — | — |

### 🔬 Chẩn đoán (Diagnosis)

| Permission | ADMIN | DOCTOR | NURSE | RECEPTIONIST | PHARMACIST |
|---|---|---|---|---|---|
| `DIAGNOSIS_CREATE` | ✅ | ✅ | — | — | — |
| `DIAGNOSIS_READ` | ✅ | ✅ | — | — | — |
| `DIAGNOSIS_UPDATE` | ✅ | ✅ | — | — | — |

### 💊 Kho thuốc (Pharmacy)

| Permission | ADMIN | DOCTOR | NURSE | RECEPTIONIST | PHARMACIST |
|---|---|---|---|---|---|
| `PHARMACY_CREATE` | ✅ | — | — | — | ✅ |
| `PHARMACY_READ` | ✅ | — | — | — | ✅ |
| `PHARMACY_UPDATE` | ✅ | — | — | — | ✅ |
| `PHARMACY_DELETE` | ✅ | — | — | — | — |

### 💵 Hóa đơn (Invoice)

| Permission | ADMIN | DOCTOR | NURSE | RECEPTIONIST | PHARMACIST |
|---|---|---|---|---|---|
| `INVOICE_CREATE` | ✅ | — | — | ✅ | — |
| `INVOICE_READ` | ✅ | — | — | ✅ | — |
| `INVOICE_UPDATE` | ✅ | — | — | ✅ | — |
| `INVOICE_DELETE` | ✅ | — | — | — | — |

### 📊 Kiểm toán (Audit)

| Permission | ADMIN | DOCTOR | NURSE | RECEPTIONIST | PHARMACIST |
|---|---|---|---|---|---|
| `AUDIT_READ` | ✅ | — | — | — | — |

### 🔐 Vai trò & Quyền (Role & Permission)

| Permission | ADMIN | DOCTOR | NURSE | RECEPTIONIST | PHARMACIST |
|---|---|---|---|---|---|
| `ROLE_READ` | ✅ | — | — | — | — |
| `ROLE_CREATE` | ✅ | — | — | — | — |
| `ROLE_UPDATE` | ✅ | — | — | — | — |
| `ROLE_DELETE` | ✅ | — | — | — | — |
| `PERMISSION_READ` | ✅ | — | — | — | — |

---

## Tổng số quyền theo vai trò

| Role | Số quyền | Nhóm chức năng |
|---|---|---|
| **ADMIN** | **35** | Toàn bộ hệ thống |
| **DOCTOR** | **20** | Bệnh nhân, Hồ sơ, Đơn thuốc, Lịch hẹn, Dấu hiệu, Chẩn đoán |
| **NURSE** | **8** | Bệnh nhân (đọc), Hồ sơ (đọc), Lịch hẹn (đọc), Dấu hiệu |
| **RECEPTIONIST** | **10** | Bệnh nhân (CRU), Lịch hẹn (CRUD), Hóa đơn (CRU) |
| **PHARMACIST** | **5** | Đơn thuốc (đọc + duyệt), Kho thuốc (CRU) |
