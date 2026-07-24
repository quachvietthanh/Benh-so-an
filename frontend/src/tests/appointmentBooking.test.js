import test from 'node:test'
import assert from 'node:assert/strict'

/**
 * Helper: Kiểm tra thông tin đặt lịch khám
 */
function validateAppointmentBooking(payload) {
  const errors = {}

  if (!payload.patientId) {
    errors.patientId = 'Vui lòng chọn bệnh nhân'
  }

  if (!payload.doctorId) {
    errors.doctorId = 'Vui lòng chọn bác sĩ khám'
  }

  if (!payload.appointmentDate) {
    errors.appointmentDate = 'Vui lòng chọn ngày khám'
  }

  if (!payload.slot) {
    errors.slot = 'Vui lòng chọn khung giờ khám'
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  }
}

/**
 * Helper: Tạo payload POST /appointments
 */
function buildAppointmentBookingPayload(input) {
  return {
    patientId: input.patientId,
    doctorId: input.doctorId,
    appointmentDate: input.appointmentDate,
    slot: input.slot,
    department: input.department?.trim() || 'Nội tổng quát',
    reason: input.reason?.trim() || null,
  }
}

test('Kiểm thử đặt lịch: Bắt buộc chọn bệnh nhân và bác sĩ', () => {
  const result = validateAppointmentBooking({
    patientId: null,
    doctorId: null,
    appointmentDate: '2026-07-25',
    slot: '09:00',
  })
  assert.equal(result.isValid, false)
  assert.equal(result.errors.patientId, 'Vui lòng chọn bệnh nhân')
  assert.equal(result.errors.doctorId, 'Vui lòng chọn bác sĩ khám')
})

test('Kiểm thử đặt lịch: Bắt buộc chọn ngày và khung giờ khám', () => {
  const result = validateAppointmentBooking({
    patientId: 'patient-123',
    doctorId: 'doctor-456',
    appointmentDate: '',
    slot: '',
  })
  assert.equal(result.isValid, false)
  assert.equal(result.errors.appointmentDate, 'Vui lòng chọn ngày khám')
  assert.equal(result.errors.slot, 'Vui lòng chọn khung giờ khám')
})

test('Kiểm thử đặt lịch: Chuẩn hóa payload POST /appointments thành công', () => {
  const input = {
    patientId: '11111111-1111-1111-1111-111111111111',
    doctorId: '22222222-2222-2222-2222-222222222222',
    appointmentDate: '2026-07-26',
    slot: '10:30',
    department: ' Tim mạch ',
    reason: ' Khám định kỳ huyết áp ',
  }

  const validation = validateAppointmentBooking(input)
  assert.equal(validation.isValid, true)

  const payload = buildAppointmentBookingPayload(input)
  assert.deepEqual(payload, {
    patientId: '11111111-1111-1111-1111-111111111111',
    doctorId: '22222222-2222-2222-2222-222222222222',
    appointmentDate: '2026-07-26',
    slot: '10:30',
    department: 'Tim mạch',
    reason: 'Khám định kỳ huyết áp',
  })
})
