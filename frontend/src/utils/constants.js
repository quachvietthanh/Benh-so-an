// Vai trò người dùng - khớp với enum bên backend
export const ROLES = {
  ADMIN: 'ADMIN',
  RECEPTIONIST: 'RECEPTIONIST', // lễ tân
  DOCTOR: 'DOCTOR', // bác sĩ
  PHARMACIST: 'PHARMACIST', // dược sĩ
  CASHIER: 'CASHIER', // thu ngân
}

export const ROLE_LABELS = {
  [ROLES.ADMIN]: 'Quản trị viên',
  [ROLES.RECEPTIONIST]: 'Lễ tân',
  [ROLES.DOCTOR]: 'Bác sĩ',
  [ROLES.PHARMACIST]: 'Dược sĩ',
  [ROLES.CASHIER]: 'Thu ngân',
}

// Trang mặc định theo từng vai trò sau khi đăng nhập (NCL-01-CN-001-TC-01)
export const ROLE_HOME_ROUTE = {
  [ROLES.ADMIN]: '/admin/accounts',
  [ROLES.RECEPTIONIST]: '/patients',
  [ROLES.DOCTOR]: '/appointments/queue',
  [ROLES.PHARMACIST]: '/pharmacy/dispense',
  [ROLES.CASHIER]: '/payments',
}

export const ACCOUNT_STATUS = {
  ACTIVE: 'ACTIVE',
  LOCKED: 'LOCKED',
}
