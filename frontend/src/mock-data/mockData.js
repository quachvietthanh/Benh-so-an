export const demoUsers = [
  {
    id: 'u1',
    username: 'admin',
    password: 'admin',
    fullName: 'Nguyễn Thị Lan',
    email: 'admin@benhsoan.vn',
    roles: ['admin'],
  },
  {
    id: 'u2',
    username: 'manager',
    password: 'manager',
    fullName: 'Trần Văn Minh',
    email: 'manager@benhsoan.vn',
    roles: ['manager'],
  },
  {
    id: 'u3',
    username: 'doctor',
    password: 'doctor',
    fullName: 'BS. Phạm Hồng Anh',
    email: 'doctor@benhsoan.vn',
    roles: ['doctor'],
  },
  {
    id: 'u4',
    username: 'receptionist',
    password: 'receptionist',
    fullName: 'Lê Thị Hạnh',
    email: 'reception@benhsoan.vn',
    roles: ['receptionist'],
  },
  {
    id: 'u5',
    username: 'pharmacist',
    password: 'pharmacist',
    fullName: 'Đặng Thị Uyên',
    email: 'pharm@benhsoan.vn',
    roles: ['pharmacist'],
  },
]

export const demoPatients = [
  {
    id: 'p1',
    patientCode: 'BN-2026001',
    fullName: 'Nguyễn Văn An',
    dateOfBirth: '1988-05-12',
    gender: 'MALE',
    phoneNumber: '0908123456',
    email: 'an.nguyen@email.com',
    address: '123 Lê Lợi, Quận 1, TP.HCM',
    identityNumber: '012345678910',
    healthInsuranceCode: 'BH123456789',
    bloodType: 'A+',
    emergencyContact: 'Nguyễn Thị Hoa - 0911123456',
    medicalHistory: 'Tăng huyết áp',
    allergies: 'Penicillin',
  },
  {
    id: 'p2',
    patientCode: 'BN-2026002',
    fullName: 'Trần Thị Bình',
    dateOfBirth: '1992-09-20',
    gender: 'FEMALE',
    phoneNumber: '0912345678',
    email: 'binh.tran@email.com',
    address: '45 Hai Bà Trưng, Quận 3, TP.HCM',
    identityNumber: '098765432109',
    healthInsuranceCode: 'BH987654321',
    bloodType: 'O+',
    emergencyContact: 'Trần Văn Cường - 0987654321',
    medicalHistory: 'Đái tháo đường',
    allergies: 'Không có',
  },
  {
    id: 'p3',
    patientCode: 'BN-2026003',
    fullName: 'Lê Minh Huy',
    dateOfBirth: '1975-01-08',
    gender: 'MALE',
    phoneNumber: '0934567890',
    email: 'huy.le@email.com',
    address: '88 Nguyễn Huệ, Quận 5, TP.HCM',
    identityNumber: '011223344556',
    healthInsuranceCode: 'BH111222333',
    bloodType: 'B+',
    emergencyContact: 'Lê Thu Hà - 0931234567',
    medicalHistory: 'Viêm khớp',
    allergies: 'Sulfonamide',
  },
]

export const demoAppointments = [
  { id: 'a1', patientId: 'p1', patientName: 'Nguyễn Văn An', doctorId: 'd1', doctorName: 'BS. Phạm Hồng Anh', slot: '08:00', date: '2026-07-15', status: 'SCHEDULED' },
  { id: 'a2', patientId: 'p2', patientName: 'Trần Thị Bình', doctorId: 'd1', doctorName: 'BS. Phạm Hồng Anh', slot: '09:30', date: '2026-07-15', status: 'CHECKED_IN' },
  { id: 'a3', patientId: 'p3', patientName: 'Lê Minh Huy', doctorId: 'd2', doctorName: 'BS. Nguyễn Minh', slot: '10:00', date: '2026-07-15', status: 'NO_SHOW' },
]

export const demoMedicalRecords = [
  {
    id: 'm1',
    recordCode: 'BA-0001',
    patientId: 'p1',
    patientName: 'Nguyễn Văn An',
    doctorName: 'BS. Phạm Hồng Anh',
    diagnosis: 'Tăng huyết áp mức độ vừa',
    status: 'COMPLETED',
    createdAt: '2026-06-30T08:00:00',
  },
  {
    id: 'm2',
    recordCode: 'BA-0002',
    patientId: 'p2',
    patientName: 'Trần Thị Bình',
    doctorName: 'BS. Phạm Hồng Anh',
    diagnosis: 'Đái tháo đường type 2',
    status: 'IN_PROGRESS',
    createdAt: '2026-07-10T09:30:00',
  },
]

export const demoMedicines = [
  { id: 'med1', name: 'Amlodipine 5mg', category: 'Tim mạch', stock: 120, minStock: 20, expiryDate: '2027-10-01', lot: 'LOT-AM-001', price: 2000 },
  { id: 'med2', name: 'Metformin 500mg', category: 'Tiểu đường', stock: 8, minStock: 20, expiryDate: '2026-08-15', lot: 'LOT-MT-002', price: 1500 },
  { id: 'med3', name: 'Paracetamol 500mg', category: 'Giảm đau', stock: 300, minStock: 50, expiryDate: '2028-01-20', lot: 'LOT-PA-003', price: 1000 },
  { id: 'med4', name: 'Ibuprofen 400mg', category: 'Giảm đau, Kháng viêm', stock: 150, minStock: 30, expiryDate: '2027-05-10', lot: 'LOT-IB-004', price: 2500 },
  { id: 'med5', name: 'Aspirin 81mg', category: 'Tim mạch', stock: 200, minStock: 40, expiryDate: '2028-02-15', lot: 'LOT-AS-005', price: 1200 },
]

export const drugInteractions = [
  {
    drugs: ['med3', 'med4'], 
    severity: 'Cảnh báo (Vừa)',
    description: 'Dùng chung Paracetamol và Ibuprofen có thể tăng nguy cơ tác dụng phụ lên dạ dày và thận. Cần cân nhắc liều lượng.'
  },
  {
    drugs: ['med4', 'med5'], 
    severity: 'Nghiêm trọng (Cao)',
    description: 'Ibuprofen làm giảm tác dụng bảo vệ tim mạch của Aspirin liều thấp và tăng nguy cơ xuất huyết tiêu hóa. Chống chỉ định dùng chung.'
  }
]

export const demoPrescriptions = [
  { id: 'pre1', recordId: 'm2', medicines: [{ id: 'med1', quantity: 30, dosage: 'Sáng 1 viên' }, { id: 'med2', quantity: 60, dosage: 'Sáng 1 viên, Tối 1 viên' }] },
]

export const demoInvoices = [
  { id: 'inv1', invoiceCode: 'HD-0001', patientName: 'Nguyễn Văn An', amount: 250000, status: 'PAID', createdAt: '2026-06-30T09:00:00', adjustmentOf: null },
  { id: 'inv2', invoiceCode: 'HD-0002', patientName: 'Trần Thị Bình', amount: 450000, status: 'PENDING', createdAt: '2026-07-10T10:30:00', adjustmentOf: null },
]

export const demoAuditLogs = [
  { id: 'log1', user: 'BS. Phạm Hồng Anh', patient: 'Nguyễn Văn An', action: 'Xem hồ sơ bệnh án', time: '2026-07-12 08:10' },
  { id: 'log2', user: 'Lê Thị Hạnh', patient: 'Trần Thị Bình', action: 'Cập nhật hồ sơ hành chính', time: '2026-07-12 09:40' },
]

export const demoServices = [
  { id: 'svc1', name: 'Khám tổng quát', price: 150000, effectiveFrom: '2026-01-01', status: 'ACTIVE' },
  { id: 'svc2', name: 'Xét nghiệm máu cơ bản', price: 220000, effectiveFrom: '2026-01-01', status: 'ACTIVE' },
  { id: 'svc3', name: 'Siêu âm bụng', price: 320000, effectiveFrom: '2026-04-01', status: 'ACTIVE' },
]
