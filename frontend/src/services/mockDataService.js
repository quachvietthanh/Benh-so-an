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
} from '../mock-data/mockData'
import {
  DashboardOutlined,
  UserOutlined,
  CalendarOutlined,
  FileTextOutlined,
  MedicineBoxOutlined,
  DollarCircleOutlined,
  BarChartOutlined,
  SettingOutlined,
  SafetyCertificateOutlined,
} from '@ant-design/icons'

const clone = (value) => JSON.parse(JSON.stringify(value))

export const roleRoutes = {
  admin: ['/', '/patients', '/appointments', '/medical-records', '/prescriptions', '/pharmacy', '/billing', '/reports', '/system-management', '/users', '/services', '/public-lookup'],
  manager: ['/', '/patients', '/medical-records', '/prescriptions', '/pharmacy', '/billing', '/reports', '/services'],
  doctor: ['/', '/patients', '/appointments', '/medical-records', '/prescriptions'],
  receptionist: ['/', '/patients', '/appointments', '/billing'],
  pharmacist: ['/', '/pharmacy', '/prescriptions'],
}

export const getNavigationItems = (roles = []) => {
  const allItems = [
    { key: '/', label: 'Tổng quan', icon: DashboardOutlined, roles: ['admin', 'manager', 'doctor', 'receptionist', 'pharmacist'] },
    { key: '/patients', label: 'Quản lý hồ sơ bệnh nhân', icon: UserOutlined, roles: ['admin', 'manager', 'doctor', 'receptionist'] },
    { key: '/appointments', label: 'Lịch hẹn và hàng đợi khám', icon: CalendarOutlined, roles: ['admin', 'doctor', 'receptionist'] },
    { key: '/medical-records', label: 'Khám bệnh & bệnh án điện tử', icon: FileTextOutlined, roles: ['admin', 'manager', 'doctor'] },
    { key: '/prescriptions', label: 'Kê đơn thuốc & cảnh báo TT', icon: MedicineBoxOutlined, roles: ['admin', 'manager', 'doctor', 'pharmacist'] },
    { key: '/pharmacy', label: 'Quản lý kho thuốc & cấp phát', icon: MedicineBoxOutlined, roles: ['admin', 'manager', 'pharmacist'] },
    { key: '/billing', label: 'Thu phí & hóa đơn', icon: DollarCircleOutlined, roles: ['admin', 'manager', 'receptionist'] },
    { key: '/reports', label: 'Báo cáo vận hành & nhật ký', icon: BarChartOutlined, roles: ['admin', 'manager'] },
    { key: '/system-management', label: 'Quản trị hệ thống & dịch vụ', icon: SettingOutlined, roles: ['admin'] },
    { key: '/public-lookup', label: 'Chăm sóc sau khám & tra cứu', icon: SafetyCertificateOutlined, roles: ['admin', 'manager', 'doctor', 'receptionist', 'pharmacist'] },
  ]

  return allItems.filter((item) => item.roles.some((role) => roles.includes(role)))
}

export const loginUser = ({ username, password }) => {
  const found = demoUsers.find((user) => user.username === username && user.password === password)
  if (!found) {
    throw new Error('Tên đăng nhập hoặc mật khẩu không đúng')
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
  revenueToday: 2850000,
})

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

export const getAppointments = () => clone(demoAppointments)

export const updateAppointmentStatus = (id, status) => {
  const appointment = demoAppointments.find((item) => item.id === id)
  if (appointment) {
    appointment.status = status
  }
  return clone(appointment)
}

export const getMedicalRecords = () => clone(demoMedicalRecords)

export const getMedicalRecordById = (id) => clone(demoMedicalRecords.find((record) => record.id === id))

export const getMedicines = () => clone(demoMedicines)

export const updateMedicineStock = (id, stockChange) => {
  const medicine = demoMedicines.find((item) => item.id === id)
  if (medicine) {
    medicine.stock = Math.max(0, medicine.stock + stockChange)
  }
  return clone(medicine)
}

export const getInvoices = () => clone(demoInvoices)

export const getAuditLogs = () => clone(demoAuditLogs)

export const getUsers = () => clone(demoUsers)

export const getServices = () => clone(demoServices)

export const getPrescriptions = () => clone(demoPrescriptions)

export const checkDrugInteractions = (selectedDrugIds) => {
  const warnings = []
  drugInteractions.forEach(interaction => {
    const hasInteraction = interaction.drugs.every(id => selectedDrugIds.includes(id))
    if (hasInteraction) {
      warnings.push(interaction)
    }
  })
  return warnings
}
