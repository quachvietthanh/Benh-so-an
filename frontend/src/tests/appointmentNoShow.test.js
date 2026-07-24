import test from 'node:test'
import assert from 'node:assert/strict'
import {
  isAppointmentOverdue,
  getOverdueMinutes,
  NO_SHOW_GRACE_MINUTES,
} from '../utils/appointmentTiming.js'

/**
 * Helper: Giả lập chuyển trạng thái Không Đến (NO_SHOW)
 */
function markAppointmentNoShow(appointment, now = new Date()) {
  if (appointment.status !== 'SCHEDULED') {
    throw new Error(`Chỉ có thể đánh dấu 'Không đến' cho lịch hẹn ở trạng thái SCHEDULED`)
  }

  const overdue = isAppointmentOverdue(appointment, now)
  if (!overdue) {
    const minutesLeft = NO_SHOW_GRACE_MINUTES - getOverdueMinutes(appointment, now)
    throw new Error(`Chưa đủ thời gian ân hạn 15 phút (Còn ${minutesLeft} phút)`)
  }

  return {
    ...appointment,
    status: 'NO_SHOW',
    updatedAt: now.toISOString(),
  }
}

test('Kiểm thử không đến: Không cho phép đánh dấu NO_SHOW khi lịch hẹn chưa quá hạn 15 phút', () => {
  const now = new Date('2026-07-24T10:05:00Z')
  const appointment = {
    id: 'apt-101',
    status: 'SCHEDULED',
    appointmentAt: '2026-07-24T10:00:00Z', // Mới quá 5 phút (< 15 phút)
  }

  assert.throws(
    () => markAppointmentNoShow(appointment, now),
    /Chưa đủ thời gian ân hạn 15 phút/
  )
})

test('Kiểm thử không đến: Đánh dấu NO_SHOW thành công khi quá hạn 15 phút', () => {
  const now = new Date('2026-07-24T10:16:00Z')
  const appointment = {
    id: 'apt-102',
    status: 'SCHEDULED',
    appointmentAt: '2026-07-24T10:00:00Z', // Quá 16 phút (> 15 phút)
  }

  const result = markAppointmentNoShow(appointment, now)
  assert.equal(result.status, 'NO_SHOW')
  assert.ok(result.updatedAt)
})

test('Kiểm thử không đến: Từ chối đánh dấu NO_SHOW nếu lịch hẹn đã được khám hoặc hủy', () => {
  const now = new Date('2026-07-24T11:00:00Z')
  const completedAppointment = {
    id: 'apt-103',
    status: 'COMPLETED',
    appointmentAt: '2026-07-24T10:00:00Z',
  }

  assert.throws(
    () => markAppointmentNoShow(completedAppointment, now),
    /Chỉ có thể đánh dấu 'Không đến' cho lịch hẹn ở trạng thái SCHEDULED/
  )
})
