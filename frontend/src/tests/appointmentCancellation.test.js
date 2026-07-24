import test from 'node:test'
import assert from 'node:assert/strict'

/**
 * Helper: Kiểm tra lý do hủy lịch hẹn
 */
function validateAppointmentCancellation(reason) {
  if (!reason || !reason.trim()) {
    return {
      isValid: false,
      message: 'Vui lòng nhập lý do hủy lịch hẹn',
    }
  }

  if (reason.trim().length < 5) {
    return {
      isValid: false,
      message: 'Lý do hủy tối thiểu 5 ký tự',
    }
  }

  return { isValid: true, reason: reason.trim() }
}

/**
 * Helper: Giả lập chuyển trạng thái hủy lịch hẹn
 */
function cancelAppointmentState(appointment, reason) {
  const allowedStatuses = ['SCHEDULED', 'CHECKED_IN']
  if (!allowedStatuses.includes(appointment.status)) {
    throw new Error(`Không thể hủy lịch hẹn đang ở trạng thái ${appointment.status}`)
  }

  const validation = validateAppointmentCancellation(reason)
  if (!validation.isValid) {
    throw new Error(validation.message)
  }

  return {
    ...appointment,
    status: 'CANCELLED',
    cancelReason: validation.reason,
    cancelledAt: new Date().toISOString(),
  }
}

test('Kiểm thử hủy lịch: Yêu cầu lý do hủy lịch hẹn hợp lệ', () => {
  const emptyCheck = validateAppointmentCancellation('  ')
  assert.equal(emptyCheck.isValid, false)
  assert.equal(emptyCheck.message, 'Vui lòng nhập lý do hủy lịch hẹn')

  const shortCheck = validateAppointmentCancellation('Bận')
  assert.equal(shortCheck.isValid, false)
  assert.equal(shortCheck.message, 'Lý do hủy tối thiểu 5 ký tự')
})

test('Kiểm thử hủy lịch: Không cho phép hủy lịch hẹn đã hoàn tất hoặc đã hủy', () => {
  const completedAppointment = { id: 'apt-1', status: 'COMPLETED' }
  assert.throws(
    () => cancelAppointmentState(completedAppointment, 'Bệnh nhân bận đột xuất'),
    /Không thể hủy lịch hẹn đang ở trạng thái COMPLETED/
  )
})

test('Kiểm thử hủy lịch: Chuyển trạng thái sang CANCELLED và lưu vết lý do thành công', () => {
  const activeAppointment = { id: 'apt-2', status: 'SCHEDULED', patientName: 'Trần Văn A' }
  const updated = cancelAppointmentState(activeAppointment, ' Bệnh nhân bận công tác đột xuất ')

  assert.equal(updated.status, 'CANCELLED')
  assert.equal(updated.cancelReason, 'Bệnh nhân bận công tác đột xuất')
  assert.ok(updated.cancelledAt)
})
