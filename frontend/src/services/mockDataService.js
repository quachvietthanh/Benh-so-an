import {
  demoUsers,
  demoPatients,
  demoAppointments,
  demoMedicalRecords,
  demoMedicines,
  demoInvoices,
  demoAuditLogs,
  demoServices,
  demoPrescriptions,
  drugInteractions,
  demoStockLogs,
  demoRevenueByDay,
} from '../mock-data/mockData'
import {
  DashboardOutlined,
  UserOutlined,
  CalendarOutlined,
  FileTextOutlined,
  MedicineBoxOutlined,
  DollarCircleOutlined,
  BarChartOutlined,
  TeamOutlined,
  SettingOutlined,
  SearchOutlined,
} from '@ant-design/icons'

const clone = (value) => JSON.parse(JSON.stringify(value))

export const roleRoutes = {
  admin: ['/', '/patients', '/appointments', '/medical-records', '/prescriptions', '/pharmacy', '/billing', '/reports', '/users', '/services', '/public-lookup'],
  manager: ['/', '/patients', '/appointments', '/medical-records', '/prescriptions', '/pharmacy', '/billing', '/reports', '/services'],
  doctor: ['/', '/patients', '/appointments', '/medical-records', '/prescriptions'],
  receptionist: ['/', '/patients', '/appointments', '/billing'],
  pharmacist: ['/', '/pharmacy', '/prescriptions'],
}

export const getNavigationItems = (roles = []) => {
  const allItems = [
    { key: '/', label: 'Tổng quan', icon: DashboardOutlined, roles: ['admin', 'manager', 'doctor', 'receptionist', 'pharmacist'] },
    { key: '/patients', label: 'Đăng kí hồ sơ bệnh nhân', icon: UserOutlined, roles: ['admin', 'manager', 'doctor', 'receptionist'] },
    { key: '/appointments', label: 'Lịch hẹn & hàng đợi khám', icon: CalendarOutlined, roles: ['admin', 'manager', 'doctor', 'receptionist'] },
    { key: '/medical-records', label: 'Khám bệnh & bệnh án điện tử', icon: FileTextOutlined, roles: ['admin', 'manager', 'doctor'] },
    { key: '/prescriptions', label: 'Kê đơn thuốc', icon: MedicineBoxOutlined, roles: ['admin', 'manager', 'doctor', 'pharmacist'] },
    { key: '/pharmacy', label: 'Quản lý kho & cấp phát thuốc', icon: MedicineBoxOutlined, roles: ['admin', 'manager', 'pharmacist'] },
    { key: '/billing', label: 'Thu phí & hóa đơn', icon: DollarCircleOutlined, roles: ['admin', 'manager', 'receptionist'] },
    { key: '/reports', label: 'Báo cáo & thống kê', icon: BarChartOutlined, roles: ['admin', 'manager'] },
    { key: '/users', label: 'Quản trị hệ thống', icon: TeamOutlined, roles: ['admin'] },
    { key: '/services', label: 'Danh mục dịch vụ', icon: SettingOutlined, roles: ['admin', 'manager'] },
    { key: '/public-lookup', label: 'Tra cứu công khai', icon: SearchOutlined, roles: ['admin', 'manager', 'receptionist'] },
  ]

  return allItems.filter((item) => item.roles.some((role) => roles.includes(role)))
}

export const loginUser = ({ username, password }) => {
  const found = demoUsers.find((user) => user.username === username && user.password === password)
  if (!found) {
    throw new Error('Tên đăng nhập hoặc mật khẩu không đúng')
  }
  if (found.isLocked) {
    throw new Error('Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.')
  }
  return {
    ...clone(found),
    token: 'demo-token',
  }
}

export const getDashboardStats = () => ({
  totalPatients: demoPatients.length,
  totalRecords: demoMedicalRecords.length,
  activeQueue: demoAppointments.filter((appointment) => appointment.status === 'CHECKED_IN').length,
  revenueToday: demoRevenueByDay[demoRevenueByDay.length - 1]?.revenue || 0,
})

// ─── Patients ────────────────────────────────────────────────────────────────
export const getPatients = (keyword = '') => {
  const q = keyword.toLowerCase()
  return clone(demoPatients.filter((patient) => !q || patient.fullName.toLowerCase().includes(q) || patient.patientCode.toLowerCase().includes(q) || patient.phoneNumber.includes(q)))
}

export const getPatientById = (id) => clone(demoPatients.find((patient) => patient.id === id))

export const createPatient = (payload) => {
  const patient = {
    id: `p${Date.now()}`,
    patientCode: `BN-${Date.now().toString().slice(-4)}`,
    ...payload,
  }
  demoPatients.unshift(patient)
  return clone(patient)
}

// ─── Appointments ─────────────────────────────────────────────────────────────
export const getAppointments = () => clone(demoAppointments)

export const updateAppointmentStatus = (id, status) => {
  const appointment = demoAppointments.find((item) => item.id === id)
  if (appointment) {
    appointment.status = status
  }
  return clone(appointment)
}

export const lookupByAppointmentCode = (code) => {
  const appointment = demoAppointments.find((a) => a.appointmentCode === code.trim().toUpperCase())
  if (!appointment) return null
  const patient = demoPatients.find((p) => p.id === appointment.patientId)
  const record = demoMedicalRecords.find((r) => r.patientId === appointment.patientId)
  return {
    appointment: clone(appointment),
    patient: patient ? { fullName: patient.fullName, patientCode: patient.patientCode, dateOfBirth: patient.dateOfBirth } : null,
    record: record ? { diagnosis: record.diagnosis, status: record.status, createdAt: record.createdAt, followUpDate: record.followUpDate } : null,
  }
}

// ─── Medical Records ──────────────────────────────────────────────────────────
export const getMedicalRecords = () => clone(demoMedicalRecords)

export const getMedicalRecordById = (id) => clone(demoMedicalRecords.find((record) => record.id === id))

export const setFollowUp = (recordId, followUpDate, followUpNote) => {
  const record = demoMedicalRecords.find((r) => r.id === recordId)
  if (record) {
    record.followUpDate = followUpDate
    record.followUpNote = followUpNote
  }
  return clone(record)
}

// ─── Medicines & Pharmacy ─────────────────────────────────────────────────────
export const getMedicines = () => clone(demoMedicines)

export const updateMedicineStock = (id, stockChange) => {
  const medicine = demoMedicines.find((item) => item.id === id)
  if (medicine) {
    medicine.stock = Math.max(0, medicine.stock + stockChange)
  }
  return clone(medicine)
}

export const addMedicineToStock = (payload) => {
  const { medicineId, quantity, lot, expiryDate, note } = payload
  const medicine = demoMedicines.find((m) => m.id === medicineId)
  if (medicine) {
    medicine.stock += quantity
    medicine.lot = lot || medicine.lot
    medicine.expiryDate = expiryDate || medicine.expiryDate
  }
  const log = {
    id: `sl${Date.now()}`,
    medicineId,
    medicineName: medicine?.name || '',
    type: 'IMPORT',
    quantity,
    lot: lot || medicine?.lot || '',
    expiryDate: expiryDate || medicine?.expiryDate || '',
    note: note || '',
    createdAt: new Date().toISOString(),
    createdBy: 'Người dùng',
  }
  demoStockLogs.unshift(log)
  return clone(log)
}

export const issueMedicineByQty = (medicineId, quantity, note) => {
  const medicine = demoMedicines.find((m) => m.id === medicineId)
  if (!medicine) throw new Error('Không tìm thấy thuốc')
  if (medicine.stock < quantity) throw new Error('Tồn kho không đủ')
  medicine.stock = Math.max(0, medicine.stock - quantity)
  const log = {
    id: `sl${Date.now()}`,
    medicineId,
    medicineName: medicine.name,
    type: 'ISSUE',
    quantity,
    lot: medicine.lot,
    expiryDate: medicine.expiryDate,
    note: note || '',
    createdAt: new Date().toISOString(),
    createdBy: 'Người dùng',
  }
  demoStockLogs.unshift(log)
  return clone(log)
}

export const getStockLogs = () => clone(demoStockLogs)

// ─── Invoices ─────────────────────────────────────────────────────────────────
export const getInvoices = () => clone(demoInvoices)

export const createInvoice = (payload) => {
  const { patientId, patientName, items, status = 'PENDING' } = payload
  const amount = items.reduce((sum, item) => sum + item.price * (item.qty || 1), 0)
  const count = demoInvoices.filter((inv) => !inv.adjustmentOf).length + 1
  const invoice = {
    id: `inv${Date.now()}`,
    invoiceCode: `HD-${String(count).padStart(4, '0')}`,
    patientId,
    patientName,
    amount,
    status,
    createdAt: new Date().toISOString(),
    adjustmentOf: null,
    adjustmentReason: null,
    items,
  }
  demoInvoices.unshift(invoice)
  return clone(invoice)
}

export const adjustInvoice = (originalId, payload) => {
  const { items, adjustmentReason } = payload
  const original = demoInvoices.find((inv) => inv.id === originalId)
  if (!original) throw new Error('Không tìm thấy hóa đơn gốc')
  const amount = items.reduce((sum, item) => sum + item.price * (item.qty || 1), 0)
  const adjInvoice = {
    id: `inv${Date.now()}`,
    invoiceCode: `${original.invoiceCode}-DC`,
    patientId: original.patientId,
    patientName: original.patientName,
    amount,
    status: 'PENDING',
    createdAt: new Date().toISOString(),
    adjustmentOf: originalId,
    adjustmentReason,
    items,
  }
  demoInvoices.unshift(adjInvoice)
  return clone(adjInvoice)
}

export const markInvoicePaid = (id) => {
  const invoice = demoInvoices.find((inv) => inv.id === id)
  if (invoice) invoice.status = 'PAID'
  return clone(invoice)
}

// ─── Audit Logs ───────────────────────────────────────────────────────────────
export const getAuditLogs = () => clone(demoAuditLogs)

export const addAuditLog = (user, patient, action, module) => {
  const log = {
    id: `log${Date.now()}`,
    user,
    patient: patient || '-',
    action,
    module: module || '',
    time: new Date().toLocaleString('vi-VN'),
  }
  demoAuditLogs.unshift(log)
  return clone(log)
}

// ─── Users ────────────────────────────────────────────────────────────────────
export const getUsers = () => clone(demoUsers)

export const createUser = (payload) => {
  const user = {
    id: `u${Date.now()}`,
    ...payload,
    isLocked: false,
    createdAt: new Date().toISOString().slice(0, 10),
  }
  demoUsers.push(user)
  return clone(user)
}

export const toggleUserLock = (id) => {
  const user = demoUsers.find((u) => u.id === id)
  if (user) user.isLocked = !user.isLocked
  return clone(user)
}

export const updateUserRoles = (id, roles) => {
  const user = demoUsers.find((u) => u.id === id)
  if (user) user.roles = roles
  return clone(user)
}

// ─── Services ─────────────────────────────────────────────────────────────────
export const getServices = () => clone(demoServices)

export const createService = (payload) => {
  const service = {
    id: `svc${Date.now()}`,
    ...payload,
    status: payload.status || 'ACTIVE',
  }
  demoServices.push(service)
  return clone(service)
}

export const updateService = (id, payload) => {
  const idx = demoServices.findIndex((s) => s.id === id)
  if (idx >= 0) {
    demoServices[idx] = { ...demoServices[idx], ...payload }
  }
  return clone(demoServices[idx])
}

export const deleteService = (id) => {
  const idx = demoServices.findIndex((s) => s.id === id)
  if (idx >= 0) demoServices.splice(idx, 1)
}

// ─── Prescriptions ────────────────────────────────────────────────────────────
export const getPrescriptions = () => clone(demoPrescriptions)

export const checkDrugInteractions = (selectedDrugIds) => {
  const warnings = []
  drugInteractions.forEach((interaction) => {
    const hasInteraction = interaction.drugs.every((id) => selectedDrugIds.includes(id))
    if (hasInteraction) {
      warnings.push(interaction)
    }
  })
  return warnings
}

// ─── Revenue Reports ──────────────────────────────────────────────────────────
export const getRevenueByDay = () => clone(demoRevenueByDay)