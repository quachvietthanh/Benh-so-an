# Database Schema - Electronic Medical Record (EMR)

## 1. Tổng quan

Tài liệu này mô tả Database Schema của hệ thống **Electronic Medical Record (EMR)**.

### Công nghệ

- Database: MySQL 8.x
- Primary Key: UUID
- Timestamp: UTC
- Quan hệ: Chuẩn hóa đến tối thiểu 3NF
- Soft Delete: Không sử dụng (ghi log thay vì xóa)
- Audit: Các thao tác quan trọng được lưu vào Audit Log

---

# 2. Authentication & Authorization

## ROLE

Lưu danh sách vai trò trong hệ thống.

| Column | Type | Constraint | Description |
|--------|------|------------|-------------|
|  id    | UUID |     PK     | ID vai trò  |
| name   | VARCHAR | UNIQUE | Tên vai trò |
| description | TEXT | | Mô tả |
| is_system | BOOLEAN | | Vai trò hệ thống |
| created_at | TIMESTAMP | | Ngày tạo |

---

## PERMISSION

Danh sách quyền.

| Column | Type | Constraint | Description |
|----------|------|------------|-------------|
| id | UUID | PK | ID |
| feature_code | VARCHAR | | Module |
| action | VARCHAR | | READ / CREATE / UPDATE / DELETE |
| description | TEXT | | Mô tả |

---

## ROLE_PERMISSION

Quan hệ nhiều-nhiều giữa Role và Permission.

| Column | Type | Constraint |
|----------|------|------------|
| role_id | UUID | PK, FK |
| permission_id | UUID | PK, FK |

---

## USER

Tài khoản người dùng.

| Column | Type | Constraint |
|----------|------|------------|
| id | UUID | PK |
| username | VARCHAR | UNIQUE |
| password_hash | VARCHAR | NOT NULL |
| full_name | VARCHAR | |
| email | VARCHAR | UNIQUE |
| phone | VARCHAR | |
| role_id | UUID | FK |
| is_active | BOOLEAN | |
| last_login_at | TIMESTAMP | |
| created_at | TIMESTAMP | |

---

## USER_SESSION

Quản lý phiên đăng nhập.

| Column | Type |
|----------|------|
| id | UUID |
| user_id | UUID FK |
| token_hash | VARCHAR |
| expires_at | TIMESTAMP |
| created_at | TIMESTAMP |

---

## LOGIN_LOG

Lưu lịch sử thao tác.

| Column | Type |
|----------|------|
| id | UUID |
| user_id | UUID FK |
| action_type | VARCHAR |
| resource_type | VARCHAR |
| resource_id | UUID |
| detail | JSON |
| created_at | TIMESTAMP |

---

# 3. Patient Management

## PATIENT

Thông tin bệnh nhân.

| Column | Type |
|----------|------|
| id | UUID |
| patient_code | VARCHAR UNIQUE |
| full_name | VARCHAR |
| date_of_birth | DATE |
| gender | VARCHAR |
| phone | VARCHAR |
| address | TEXT |
| id_number | VARCHAR |
| emergency_contact | VARCHAR |
| created_by | UUID FK |
| created_at | TIMESTAMP |

---

## PATIENT_CHANGE_LOG

Lưu lịch sử chỉnh sửa hồ sơ.

| Column | Type |
|----------|------|
| id | UUID |
| patient_id | UUID FK |
| changed_by | UUID FK |
| old_data | JSON |
| new_data | JSON |
| changed_at | TIMESTAMP |

---

# 4. Service Catalog

## SERVICE_CATALOG

Danh mục dịch vụ.

| Column | Type |
|----------|------|
| id | UUID |
| name | VARCHAR |
| description | TEXT |
| is_active | BOOLEAN |
| created_at | TIMESTAMP |

---

## SERVICE_PRICE

Lịch sử giá dịch vụ.

| Column | Type |
|----------|------|
| id | UUID |
| service_id | UUID FK |
| price | DECIMAL |
| effective_from | DATE |
| effective_to | DATE |
| created_by | UUID FK |

---

# 5. Appointment

## APPOINTMENT

Thông tin lịch hẹn.

| Column | Type |
|----------|------|
| id | UUID |
| patient_id | UUID FK |
| doctor_id | UUID FK |
| scheduled_at | DATETIME |
| status | VARCHAR |
| cancellation_reason | TEXT |
| created_by | UUID FK |
| created_at | TIMESTAMP |

---

## APPOINTMENT_REMINDER

Nhắc lịch hẹn.

| Column | Type |
|----------|------|
| id | UUID |
| appointment_id | UUID FK |
| remind_at | DATETIME |
| channel | VARCHAR |
| status | VARCHAR |
| sent_at | TIMESTAMP |

---

## VISIT_QUEUE

Hàng đợi khám.

| Column | Type |
|----------|------|
| id | UUID |
| appointment_id | UUID FK |
| patient_id | UUID FK |
| doctor_id | UUID FK |
| queue_number | INT |
| status | VARCHAR |
| checked_in_at | TIMESTAMP |
| called_at | TIMESTAMP |

---

# 6. Electronic Medical Record

## VISIT

Một lần khám bệnh.

| Column | Type |
|----------|------|
| id | UUID |
| patient_id | UUID FK |
| doctor_id | UUID FK |
| appointment_id | UUID FK |
| queue_id | UUID FK |
| chief_complaint | TEXT |
| physical_exam | TEXT |
| notes | TEXT |
| status | VARCHAR |
| is_locked | BOOLEAN |
| started_at | TIMESTAMP |
| ended_at | TIMESTAMP |
| created_at | TIMESTAMP |

---

## DIAGNOSIS

Chuẩn đoán.

| Column | Type |
|----------|------|
| id | UUID |
| visit_id | UUID FK |
| icd_code | VARCHAR |
| description | TEXT |
| instructions | TEXT |
| follow_up_date | DATE |
| created_by | UUID FK |
| created_at | TIMESTAMP |

---

## LAB_ORDER

Chỉ định xét nghiệm.

| Column | Type |
|----------|------|
| id | UUID |
| visit_id | UUID FK |
| order_type | VARCHAR |
| item_name | VARCHAR |
| status | VARCHAR |
| ordered_by | UUID FK |
| ordered_at | TIMESTAMP |

---

## LAB_RESULT

Kết quả xét nghiệm.

| Column | Type |
|----------|------|
| id | UUID |
| lab_order_id | UUID FK |
| value_text | TEXT |
| value_numeric | DECIMAL |
| unit | VARCHAR |
| file_path | VARCHAR |
| entered_by | UUID FK |
| entered_at | TIMESTAMP |

---

## MEDICAL_RECORD_ACCESS_LOG

Lưu lịch sử truy cập bệnh án.

| Column | Type |
|----------|------|
| id | UUID |
| user_id | UUID FK |
| patient_id | UUID FK |
| visit_id | UUID FK |
| action | VARCHAR |
| accessed_at | TIMESTAMP |

---

# 7. Prescription

## MEDICINE

Danh mục thuốc.

| Column | Type |
|----------|------|
| id | UUID |
| name | VARCHAR |
| active_ingredient | VARCHAR |
| unit | VARCHAR |
| description | TEXT |
| is_active | BOOLEAN |
| min_stock_qty | INT |
| created_at | TIMESTAMP |

---

## DRUG_INTERACTION

Tương tác thuốc.

| Column | Type |
|----------|------|
| id | UUID |
| medicine_a_id | UUID FK |
| medicine_b_id | UUID FK |
| severity | VARCHAR |
| description | TEXT |

---

## PRESCRIPTION

Đơn thuốc.

| Column | Type |
|----------|------|
| id | UUID |
| visit_id | UUID FK |
| status | VARCHAR |
| prescribed_by | UUID FK |
| prescribed_at | TIMESTAMP |

---

## PRESCRIPTION_ITEM

Chi tiết đơn thuốc.

| Column | Type |
|----------|------|
| id | UUID |
| prescription_id | UUID FK |
| medicine_id | UUID FK |
| dosage | DECIMAL |
| frequency | VARCHAR |
| duration_days | INT |
| quantity | INT |
| notes | TEXT |

---

## PRESCRIPTION_WARNING_LOG

Lưu cảnh báo tương tác thuốc.

| Column | Type |
|----------|------|
| id | UUID |
| prescription_id | UUID FK |
| medicine_a_id | UUID FK |
| medicine_b_id | UUID FK |
| severity | VARCHAR |
| override_reason | TEXT |
| overridden_by | UUID FK |
| created_at | TIMESTAMP |

---

# 8. Inventory

## STOCK_BATCH

Quản lý lô thuốc.

| Column | Type |
|----------|------|
| id | UUID |
| medicine_id | UUID FK |
| batch_number | VARCHAR |
| expiry_date | DATE |
| quantity | INT |
| received_by | UUID FK |
| received_at | TIMESTAMP |

---

## STOCK_RECEIPT

Phiếu nhập kho.

| Column | Type |
|----------|------|
| id | UUID |
| reference_number | VARCHAR |
| received_by | UUID FK |
| received_at | TIMESTAMP |
| notes | TEXT |

---

## STOCK_RECEIPT_ITEM

Chi tiết nhập kho.

| Column | Type |
|----------|------|
| id | UUID |
| receipt_id | UUID FK |
| medicine_id | UUID FK |
| batch_id | UUID FK |
| quantity | INT |
| unit_price | DECIMAL |

---

## DISPENSE_RECORD

Phiếu cấp phát thuốc.

| Column | Type |
|----------|------|
| id | UUID |
| prescription_id | UUID FK |
| dispensed_by | UUID FK |
| dispensed_at | TIMESTAMP |

---

## DISPENSE_ITEM

Chi tiết cấp phát.

| Column | Type |
|----------|------|
| id | UUID |
| dispense_id | UUID FK |
| prescription_item_id | UUID FK |
| batch_id | UUID FK |
| quantity | INT |

---

# 9. Billing

## PAYMENT

Thanh toán.

| Column | Type |
|----------|------|
| id | UUID |
| visit_id | UUID FK |
| exam_fee | DECIMAL |
| medicine_fee | DECIMAL |
| total_amount | DECIMAL |
| amount_paid | DECIMAL |
| payment_method | VARCHAR |
| status | VARCHAR |
| collected_by | UUID FK |
| paid_at | TIMESTAMP |

---

## INVOICE

Hóa đơn.

| Column | Type |
|----------|------|
| id | UUID |
| visit_id | UUID FK |
| payment_id | UUID FK |
| invoice_number | VARCHAR |
| type | VARCHAR |
| original_invoice_id | UUID FK |
| adjustment_reason | TEXT |
| total_amount | DECIMAL |
| created_by | UUID FK |
| created_at | TIMESTAMP |

---

## INVOICE_LINE

Chi tiết hóa đơn.

| Column | Type |
|----------|------|
| id | UUID |
| invoice_id | UUID FK |
| item_type | VARCHAR |
| item_name | VARCHAR |
| service_id | UUID FK |
| medicine_id | UUID FK |
| quantity | INT |
| unit_price | DECIMAL |
| amount | DECIMAL |

---

# 10. After Care

## FOLLOWUP_REMINDER

Nhắc tái khám.

| Column | Type |
|----------|------|
| id | UUID |
| patient_id | UUID FK |
| visit_id | UUID FK |
| remind_date | DATE |
| note | TEXT |
| status | VARCHAR |
| created_by | UUID FK |
| created_at | TIMESTAMP |

---

## AFTERCARE_NOTE

Theo dõi sau khám.

| Column | Type |
|----------|------|
| id | UUID |
| patient_id | UUID FK |
| visit_id | UUID FK |
| content | TEXT |
| contact_result | TEXT |
| recorded_by | UUID FK |
| recorded_at | TIMESTAMP |

---

# 11. System Configuration

## CLINIC_CONFIG

Thông tin phòng khám.

| Column | Type |
|----------|------|
| id | UUID |
| name | VARCHAR |
| address | TEXT |
| phone | VARCHAR |
| working_hours | JSON |
| created_at | TIMESTAMP |

---

## BACKUP_LOG

Lịch sử sao lưu.

| Column | Type |
|----------|------|
| id | UUID |
| file_path | VARCHAR |
| status | VARCHAR |
| created_by | UUID FK |
| created_at | TIMESTAMP |
| restored_at | TIMESTAMP |

---

# 12. Quan hệ chính

```
ROLE
 └── USER
      ├── USER_SESSION
      ├── AUDIT_LOG
      ├── VISIT
      ├── APPOINTMENT
      └── PATIENT

PATIENT
 ├── APPOINTMENT
 ├── VISIT
 ├── FOLLOWUP_REMINDER
 ├── AFTERCARE_NOTE
 └── PATIENT_CHANGE_LOG

VISIT
 ├── DIAGNOSIS
 ├── LAB_ORDER
 │      └── LAB_RESULT
 ├── PRESCRIPTION
 │      └── PRESCRIPTION_ITEM
 ├── PAYMENT
 ├── INVOICE
 └── MEDICAL_RECORD_ACCESS_LOG

MEDICINE
 ├── PRESCRIPTION_ITEM
 ├── DRUG_INTERACTION
 ├── STOCK_BATCH
 ├── STOCK_RECEIPT_ITEM
 └── INVOICE_LINE

PRESCRIPTION
 ├── PRESCRIPTION_ITEM
 ├── DISPENSE_RECORD
 │      └── DISPENSE_ITEM
 └── PRESCRIPTION_WARNING_LOG
```

---

# 13. Quy ước đặt tên

- Tên bảng: UPPER_SNAKE_CASE
- Primary Key: `id`
- Foreign Key: `<entity>_id`
- Thời gian tạo: `created_at`
- Người tạo: `created_by`
- Trạng thái: `status`
- Boolean: tiền tố `is_`
- UUID dùng làm Primary Key cho toàn bộ bảng.