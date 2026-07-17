export const demoUsers = [
  {
    id: 'u1',
    username: 'admin',
    password: 'admin',
    fullName: 'Nguyễn Thị Lan',
    email: 'admin@benhsoan.vn',
    roles: ['admin'],
    isLocked: false,
    createdAt: '2026-01-01',
  },
  {
    id: 'u2',
    username: 'manager',
    password: 'manager',
    fullName: 'Trần Văn Minh',
    email: 'manager@benhsoan.vn',
    roles: ['manager'],
    isLocked: false,
    createdAt: '2026-01-05',
  },
  {
    id: 'u3',
    username: 'doctor',
    password: 'doctor',
    fullName: 'BS. Phạm Hồng Anh',
    email: 'doctor@benhsoan.vn',
    roles: ['doctor'],
    isLocked: false,
    createdAt: '2026-01-10',
  },
  {
    id: 'u4',
    username: 'receptionist',
    password: 'receptionist',
    fullName: 'Lê Thị Hạnh',
    email: 'reception@benhsoan.vn',
    roles: ['receptionist'],
    isLocked: false,
    createdAt: '2026-02-01',
  },
  {
    id: 'u5',
    username: 'pharmacist',
    password: 'pharmacist',
    fullName: 'Đặng Thị Uyên',
    email: 'pharm@benhsoan.vn',
    roles: ['pharmacist'],
    isLocked: false,
    createdAt: '2026-02-15',
  },
  {
    id: 'u6',
    username: 'doctor2',
    password: 'doctor2',
    fullName: 'BS. Nguyễn Minh',
    email: 'doctor2@benhsoan.vn',
    roles: ['doctor'],
    isLocked: true,
    createdAt: '2026-03-01',
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
  {
    id: 'p4',
    patientCode: 'BN-2026004',
    fullName: 'Phạm Thị Cúc',
    dateOfBirth: '2000-03-22',
    gender: 'FEMALE',
    phoneNumber: '0945678901',
    email: 'cuc.pham@email.com',
    address: '22 Trần Phú, Quận Hải Châu, Đà Nẵng',
    identityNumber: '020304050607',
    healthInsuranceCode: 'BH202030405',
    bloodType: 'AB+',
    emergencyContact: 'Phạm Văn Hùng - 0901234567',
    medicalHistory: 'Không',
    allergies: 'Không có',
  },
]

export const demoAppointments = [
  { id: 'a1', appointmentCode: 'HEN-001', patientId: 'p1', patientName: 'Nguyễn Văn An', doctorId: 'd1', doctorName: 'BS. Phạm Hồng Anh', slot: '08:00', date: '2026-07-15', status: 'COMPLETED' },
  { id: 'a2', appointmentCode: 'HEN-002', patientId: 'p2', patientName: 'Trần Thị Bình', doctorId: 'd1', doctorName: 'BS. Phạm Hồng Anh', slot: '09:30', date: '2026-07-15', status: 'CHECKED_IN' },
  { id: 'a3', appointmentCode: 'HEN-003', patientId: 'p3', patientName: 'Lê Minh Huy', doctorId: 'd2', doctorName: 'BS. Nguyễn Minh', slot: '10:00', date: '2026-07-15', status: 'NO_SHOW' },
  { id: 'a4', appointmentCode: 'HEN-004', patientId: 'p4', patientName: 'Phạm Thị Cúc', doctorId: 'd1', doctorName: 'BS. Phạm Hồng Anh', slot: '14:00', date: '2026-07-16', status: 'SCHEDULED' },
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
    followUpDate: '2026-07-30',
    followUpNote: 'Tái khám kiểm tra huyết áp sau 1 tháng điều trị.',
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
    followUpDate: null,
    followUpNote: null,
  },
  {
    id: 'm3',
    recordCode: 'BA-0003',
    patientId: 'p3',
    patientName: 'Lê Minh Huy',
    doctorName: 'BS. Nguyễn Minh',
    diagnosis: 'Viêm khớp dạng thấp',
    status: 'COMPLETED',
    createdAt: '2026-07-12T10:00:00',
    followUpDate: '2026-08-12',
    followUpNote: 'Tái khám sau 1 tháng, xét nghiệm máu trước khi đến.',
  },
]

export const demoMedicines = [
  { id: 'med1', name: 'Amlodipine 5mg', category: 'Tim mạch', stock: 120, minStock: 20, expiryDate: '2027-10-01', lot: 'LOT-AM-001', price: 2000 },
  { id: 'med2', name: 'Metformin 500mg', category: 'Tiểu đường', stock: 8, minStock: 20, expiryDate: '2026-08-15', lot: 'LOT-MT-002', price: 1500 },
  { id: 'med3', name: 'Paracetamol 500mg', category: 'Giảm đau', stock: 300, minStock: 50, expiryDate: '2028-01-20', lot: 'LOT-PA-003', price: 1000 },
  { id: 'med4', name: 'Ibuprofen 400mg', category: 'Giảm đau, Kháng viêm', stock: 150, minStock: 30, expiryDate: '2027-05-10', lot: 'LOT-IB-004', price: 2500 },
  { id: 'med5', name: 'Aspirin 81mg', category: 'Tim mạch', stock: 200, minStock: 40, expiryDate: '2028-02-15', lot: 'LOT-AS-005', price: 1200 },
  { id: 'med6', name: 'Atorvastatin 20mg', category: 'Mỡ máu', stock: 5, minStock: 30, expiryDate: '2027-03-10', lot: 'LOT-AT-006', price: 5000 },
  { id: 'med7', name: 'Omeprazole 20mg', category: 'Dạ dày', stock: 250, minStock: 50, expiryDate: '2028-06-01', lot: 'LOT-OM-007', price: 3000 },
  { id: 'med8', name: 'Amoxicillin 500mg', category: 'Kháng sinh', stock: 0, minStock: 50, expiryDate: '2026-12-01', lot: 'LOT-AX-008', price: 4500 },
]

export const demoStockLogs = [
  { id: 'sl1', medicineId: 'med1', medicineName: 'Amlodipine 5mg', type: 'IMPORT', quantity: 100, lot: 'LOT-AM-001', expiryDate: '2027-10-01', note: 'Nhập kho định kỳ tháng 6', createdAt: '2026-06-01T09:00:00', createdBy: 'Đặng Thị Uyên' },
  { id: 'sl2', medicineId: 'med3', medicineName: 'Paracetamol 500mg', type: 'IMPORT', quantity: 200, lot: 'LOT-PA-003', expiryDate: '2028-01-20', note: 'Bổ sung tồn kho', createdAt: '2026-07-01T08:30:00', createdBy: 'Đặng Thị Uyên' },
  { id: 'sl3', medicineId: 'med2', medicineName: 'Metformin 500mg', type: 'ISSUE', quantity: 30, lot: 'LOT-MT-002', expiryDate: '2026-08-15', note: 'Cấp theo đơn BA-0002', createdAt: '2026-07-10T10:00:00', createdBy: 'Đặng Thị Uyên' },
]

export const drugInteractions = [
  {
    drugs: ['med3', 'med4'],
    severity: 'Cảnh báo (Vừa)',
    description: 'Dùng chung Paracetamol và Ibuprofen có thể tăng nguy cơ tác dụng phụ lên dạ dày và thận. Cần cân nhắc liều lượng.',
  },
  {
    drugs: ['med4', 'med5'],
    severity: 'Nghiêm trọng (Cao)',
    description: 'Ibuprofen làm giảm tác dụng bảo vệ tim mạch của Aspirin liều thấp và tăng nguy cơ xuất huyết tiêu hóa. Chống chỉ định dùng chung.',
  },
]

export const demoPrescriptions = [
  { id: 'pre1', recordId: 'm2', medicines: [{ id: 'med1', quantity: 30, dosage: 'Sáng 1 viên' }, { id: 'med2', quantity: 60, dosage: 'Sáng 1 viên, Tối 1 viên' }] },
]

export const demoInvoices = [
  { id: 'inv1', invoiceCode: 'HD-0001', patientId: 'p1', patientName: 'Nguyễn Văn An', amount: 250000, status: 'PAID', createdAt: '2026-06-30T09:00:00', adjustmentOf: null, adjustmentReason: null, items: [{ serviceId: 'svc1', serviceName: 'Khám tổng quát', price: 150000, qty: 1 }, { serviceId: 'svc2', serviceName: 'Xét nghiệm máu cơ bản', price: 100000, qty: 1 }] },
  { id: 'inv2', invoiceCode: 'HD-0002', patientId: 'p2', patientName: 'Trần Thị Bình', amount: 450000, status: 'PENDING', createdAt: '2026-07-10T10:30:00', adjustmentOf: null, adjustmentReason: null, items: [{ serviceId: 'svc1', serviceName: 'Khám tổng quát', price: 150000, qty: 1 }, { serviceId: 'svc3', serviceName: 'Siêu âm bụng', price: 300000, qty: 1 }] },
  { id: 'inv3', invoiceCode: 'HD-0001-DC', patientId: 'p1', patientName: 'Nguyễn Văn An', amount: 200000, status: 'PAID', createdAt: '2026-07-01T14:00:00', adjustmentOf: 'inv1', adjustmentReason: 'Hoàn tiền phần xét nghiệm do kết quả không đạt yêu cầu kỹ thuật', items: [{ serviceId: 'svc1', serviceName: 'Khám tổng quát', price: 200000, qty: 1 }] },
]

export const demoAuditLogs = [
  { id: 'log1', user: 'BS. Phạm Hồng Anh', patient: 'Nguyễn Văn An', action: 'Xem hồ sơ bệnh án', time: '2026-07-12 08:10', module: 'medical-records' },
  { id: 'log2', user: 'Lê Thị Hạnh', patient: 'Trần Thị Bình', action: 'Cập nhật hồ sơ hành chính', time: '2026-07-12 09:40', module: 'patients' },
  { id: 'log3', user: 'BS. Phạm Hồng Anh', patient: 'Trần Thị Bình', action: 'Kê đơn thuốc', time: '2026-07-12 10:05', module: 'prescriptions' },
  { id: 'log4', user: 'Đặng Thị Uyên', patient: 'Trần Thị Bình', action: 'Cấp phát thuốc', time: '2026-07-12 10:30', module: 'pharmacy' },
  { id: 'log5', user: 'Lê Thị Hạnh', patient: 'Nguyễn Văn An', action: 'Lập hóa đơn', time: '2026-07-13 08:00', module: 'billing' },
  { id: 'log6', user: 'Nguyễn Thị Lan', patient: '-', action: 'Tạo tài khoản mới', time: '2026-07-14 09:00', module: 'users' },
  { id: 'log7', user: 'BS. Phạm Hồng Anh', patient: 'Lê Minh Huy', action: 'Xem hồ sơ bệnh án', time: '2026-07-15 11:00', module: 'medical-records' },
  { id: 'log8', user: 'Lê Thị Hạnh', patient: 'Phạm Thị Cúc', action: 'Đăng ký hồ sơ bệnh nhân mới', time: '2026-07-15 14:10', module: 'patients' },
]

export const demoServices = [
  { id: 'svc1', name: 'Khám tổng quát', price: 150000, effectiveFrom: '2026-01-01', status: 'ACTIVE', category: 'Khám bệnh', description: 'Khám và tư vấn sức khỏe tổng quát' },
  { id: 'svc2', name: 'Xét nghiệm máu cơ bản', price: 220000, effectiveFrom: '2026-01-01', status: 'ACTIVE', category: 'Xét nghiệm', description: 'CBC, glucose, lipid máu cơ bản' },
  { id: 'svc3', name: 'Siêu âm bụng', price: 320000, effectiveFrom: '2026-04-01', status: 'ACTIVE', category: 'Chẩn đoán hình ảnh', description: 'Siêu âm tổng quát ổ bụng' },
  { id: 'svc4', name: 'Điện tâm đồ (ECG)', price: 180000, effectiveFrom: '2026-01-01', status: 'ACTIVE', category: 'Chẩn đoán hình ảnh', description: 'Ghi điện tâm đồ 12 chuyển đạo' },
  { id: 'svc5', name: 'Chụp X-quang ngực thẳng', price: 250000, effectiveFrom: '2026-01-01', status: 'INACTIVE', category: 'Chẩn đoán hình ảnh', description: 'X-quang phổi tiêu chuẩn' },
]

export const demoRevenueByDay = [
  { date: '2026-07-10', revenue: 1850000, visits: 12 },
  { date: '2026-07-11', revenue: 2100000, visits: 15 },
  { date: '2026-07-12', revenue: 1650000, visits: 11 },
  { date: '2026-07-13', revenue: 2400000, visits: 18 },
  { date: '2026-07-14', revenue: 900000, visits: 6 },
  { date: '2026-07-15', revenue: 2850000, visits: 20 },
  { date: '2026-07-16', revenue: 1200000, visits: 8 },
]
