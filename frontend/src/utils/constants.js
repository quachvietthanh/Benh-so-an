// ===== API =====
export const API_TIMEOUT = 30000

// ===== Pagination =====
export const DEFAULT_PAGE_SIZE = 10
export const PAGE_SIZE_OPTIONS = [5, 10, 20, 50]

// ===== Gender =====
export const GENDER_OPTIONS = [
  { value: 'MALE', label: 'Nam' },
  { value: 'FEMALE', label: 'Nữ' },
  { value: 'OTHER', label: 'Khác' },
]

// ===== Record Status =====
export const RECORD_STATUS_OPTIONS = [
  { value: 'NEW', label: 'Mới', color: 'blue' },
  { value: 'IN_PROGRESS', label: 'Đang xử lý', color: 'orange' },
  { value: 'COMPLETED', label: 'Hoàn thành', color: 'green' },
  { value: 'CANCELLED', label: 'Đã hủy', color: 'red' },
]

// ===== User Roles =====
export const ROLE_OPTIONS = [
  { value: 'ADMIN', label: 'Quản trị viên' },
  { value: 'DOCTOR', label: 'Bác sĩ' },
  { value: 'NURSE', label: 'Y tá' },
  { value: 'STAFF', label: 'Nhân viên' },
]

// ===== Blood Types =====
export const BLOOD_TYPES = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-']
