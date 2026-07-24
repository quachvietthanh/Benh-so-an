import test from 'node:test'
import assert from 'node:assert/strict'

/**
 * Helper: Kiểm tra tính hợp lệ của dữ liệu đăng ký bệnh nhân
 */
function validatePatientRegistration(payload) {
  const errors = {}

  if (!payload.fullName || !payload.fullName.trim()) {
    errors.fullName = 'Vui lòng nhập họ và tên bệnh nhân'
  } else if (payload.fullName.trim().length > 100) {
    errors.fullName = 'Họ tên tối đa 100 ký tự'
  }

  if (!payload.dateOfBirth) {
    errors.dateOfBirth = 'Vui lòng chọn ngày sinh'
  }

  if (!payload.gender || !['MALE', 'FEMALE', 'OTHER'].includes(payload.gender)) {
    errors.gender = 'Giới tính không hợp lệ'
  }

  if (!payload.phone) {
    errors.phone = 'Vui lòng nhập số điện thoại'
  } else if (!/^0\d{9}$/.test(payload.phone.trim())) {
    errors.phone = 'Số điện thoại gồm 10 chữ số và bắt đầu bằng số 0'
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  }
}

/**
 * Helper: Tạo payload chuẩn gửi API POST /patients
 */
function buildRegisterPayload(input) {
  return {
    fullName: input.fullName?.trim(),
    dateOfBirth: input.dateOfBirth,
    gender: input.gender,
    phone: input.phone?.trim(),
    address: input.address?.trim() || null,
    insuranceCardNumber: input.insuranceCardNumber?.trim() || null,
    medicalHistory: input.medicalHistory?.trim() || null,
  }
}

test('Kiểm thử đăng ký bệnh nhân: Từ chối họ tên rỗng', () => {
  const result = validatePatientRegistration({
    fullName: '   ',
    dateOfBirth: '1990-05-15',
    gender: 'MALE',
    phone: '0912345678',
  })
  assert.equal(result.isValid, false)
  assert.equal(result.errors.fullName, 'Vui lòng nhập họ và tên bệnh nhân')
})

test('Kiểm thử đăng ký bệnh nhân: Từ chối số điện thoại sai định dạng', () => {
  const result = validatePatientRegistration({
    fullName: 'Nguyễn Văn A',
    dateOfBirth: '1990-05-15',
    gender: 'MALE',
    phone: '123456', // Sai định dạng
  })
  assert.equal(result.isValid, false)
  assert.equal(result.errors.phone, 'Số điện thoại gồm 10 chữ số và bắt đầu bằng số 0')
})

test('Kiểm thử đăng ký bệnh nhân: Chấp nhận thông tin hợp lệ và tạo payload POST /patients đúng chuẩn', () => {
  const input = {
    fullName: ' Tran Van B ',
    dateOfBirth: '1995-10-20',
    gender: 'FEMALE',
    phone: '0987654321 ',
    address: ' 123 Nguyen Hue, Quan 1 ',
    insuranceCardNumber: ' DN4791234567890 ',
    medicalHistory: ' Dị ứng penicillin ',
  }

  const validation = validatePatientRegistration(input)
  assert.equal(validation.isValid, true)

  const payload = buildRegisterPayload(input)
  assert.deepEqual(payload, {
    fullName: 'Tran Van B',
    dateOfBirth: '1995-10-20',
    gender: 'FEMALE',
    phone: '0987654321',
    address: '123 Nguyen Hue, Quan 1',
    insuranceCardNumber: 'DN4791234567890',
    medicalHistory: 'Dị ứng penicillin',
  })
})
