import React, { useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { Alert, Button, DatePicker, Input } from 'antd'
import {
  ArrowLeftOutlined,
  CalendarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined,
  InfoCircleOutlined,
  LockOutlined,
  LoginOutlined,
  MedicineBoxOutlined,
  SafetyCertificateOutlined,
  SearchOutlined,
} from '@ant-design/icons'
import dayjs from 'dayjs'

import publicLookupApi from '../api/publicLookupApi'
import {
  getLookupErrorMessage,
  getPublicLookupStatus,
  normalizeAppointmentCode,
  validateLookupInput,
} from '../utils/publicLookup'
import './publicLookup.css'

const getStatusIcon = (careState) => {
  if (careState === 'COMPLETED') return <CheckCircleOutlined />
  if (careState === 'UNAVAILABLE') return <CloseCircleOutlined />
  if (careState === 'IN_PROGRESS') return <ClockCircleOutlined />
  return <CalendarOutlined />
}

function PublicLookupPage() {
  const appointmentCodeInput = useRef(null)
  const dateOfBirthInput = useRef(null)
  const [appointmentCode, setAppointmentCode] = useState('')
  const [dateOfBirth, setDateOfBirth] = useState(null)
  const [fieldErrors, setFieldErrors] = useState({})
  const [lookupResult, setLookupResult] = useState(null)
  const [errorMessage, setErrorMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const requestSequence = useRef(0)

  const invalidateResult = () => {
    requestSequence.current += 1
    setLookupResult(null)
    setErrorMessage('')
  }

  const handleCodeChange = (event) => {
    invalidateResult()
    setAppointmentCode(event.target.value.toUpperCase())
    setFieldErrors((current) => ({ ...current, appointmentCode: undefined }))
  }

  const handleDateChange = (value) => {
    invalidateResult()
    setDateOfBirth(value)
    setFieldErrors((current) => ({ ...current, dateOfBirth: undefined }))
  }

  const handleLookup = async (event) => {
    event.preventDefault()
    if (loading) return

    const normalizedCode = normalizeAppointmentCode(appointmentCode)
    const normalizedDateOfBirth = dateOfBirth?.format('YYYY-MM-DD') || ''
    const validationErrors = validateLookupInput({
      appointmentCode: normalizedCode,
      dateOfBirth: normalizedDateOfBirth,
    })

    setAppointmentCode(normalizedCode)
    setFieldErrors(validationErrors)
    setLookupResult(null)
    setErrorMessage('')
    if (Object.keys(validationErrors).length) {
      if (validationErrors.appointmentCode) appointmentCodeInput.current?.focus()
      else dateOfBirthInput.current?.focus()
      return
    }

    const requestId = requestSequence.current + 1
    requestSequence.current = requestId
    setLoading(true)

    try {
      const response = await publicLookupApi.lookupAppointment({
        appointmentCode: normalizedCode,
        dateOfBirth: normalizedDateOfBirth,
      })
      if (requestSequence.current !== requestId) return

      if (!response.data?.matched) {
        setErrorMessage('Không tìm thấy lịch hẹn phù hợp. Vui lòng kiểm tra lại mã hẹn và ngày sinh.')
        return
      }

      if (!response.data?.careState || !response.data?.scheduledAt) {
        setErrorMessage(getLookupErrorMessage(500))
        return
      }

      setLookupResult({
        ...response.data,
        appointmentCode: normalizedCode,
      })
    } catch (error) {
      if (requestSequence.current !== requestId) return
      setErrorMessage(getLookupErrorMessage(error.response?.status))
    } finally {
      if (requestSequence.current === requestId) setLoading(false)
    }
  }

  const resetLookup = () => {
    requestSequence.current += 1
    setAppointmentCode('')
    setDateOfBirth(null)
    setFieldErrors({})
    setLookupResult(null)
    setErrorMessage('')
    setLoading(false)
  }

  const status = lookupResult ? getPublicLookupStatus(lookupResult.careState) : null
  const scheduledAt = lookupResult ? dayjs(lookupResult.scheduledAt) : null

  return (
    <div className="public-lookup-page">
      <div className="public-lookup-decoration public-lookup-decoration-one" aria-hidden="true" />
      <div className="public-lookup-decoration public-lookup-decoration-two" aria-hidden="true" />

      <header className="public-lookup-header">
        <div className="public-lookup-header-inner">
          <Link className="public-lookup-brand" to="/public-lookup" aria-label="Bệnh Án Số - Cổng tra cứu">
            <span className="public-lookup-brand-icon"><MedicineBoxOutlined /></span>
            <span>
              <strong>BỆNH ÁN SỐ</strong>
              <small>Cổng thông tin bệnh nhân</small>
            </span>
          </Link>

          <div className="public-lookup-header-actions">
            <span className="public-lookup-secure"><SafetyCertificateOutlined /> Tra cứu bảo mật</span>
            <Link className="public-lookup-login" to="/login"><LoginOutlined /> Đăng nhập nhân viên</Link>
          </div>
        </div>
      </header>

      <main className="public-lookup-main">
        <section className="public-lookup-intro" aria-labelledby="lookup-title">
          <span className="public-lookup-eyebrow"><SafetyCertificateOutlined /> Xác minh an toàn</span>
          <h1 id="lookup-title">Tra cứu lịch hẹn khám</h1>
          <p>
            Kiểm tra thời gian và trạng thái lịch hẹn bằng mã hẹn cùng ngày sinh đã đăng ký.
          </p>

          <div className="public-lookup-steps" aria-label="Hướng dẫn tra cứu">
            <div><b>1</b><span><strong>Nhập mã lịch hẹn</strong><small>Mã được cung cấp khi đặt lịch.</small></span></div>
            <div><b>2</b><span><strong>Xác minh ngày sinh</strong><small>Giúp bảo vệ thông tin của bạn.</small></span></div>
            <div><b>3</b><span><strong>Xem trạng thái</strong><small>Nhận thông tin lịch hẹn hiện tại.</small></span></div>
          </div>

          <div className="public-lookup-trust-note">
            <LockOutlined />
            <span>
              <strong>Thông tin của bạn được bảo vệ</strong>
              <small>Cổng công khai không hiển thị chẩn đoán, đơn thuốc hoặc nội dung bệnh án.</small>
            </span>
          </div>
        </section>

        <section className="public-lookup-card" aria-labelledby="lookup-form-title">
          <div className="public-lookup-card-heading">
            <span><SearchOutlined /></span>
            <div>
              <h2 id="lookup-form-title">Thông tin tra cứu</h2>
              <p>Vui lòng nhập đầy đủ hai thông tin bên dưới.</p>
            </div>
          </div>

          <form className="public-lookup-form" onSubmit={handleLookup} noValidate aria-busy={loading}>
            <div className="public-lookup-field">
              <label htmlFor="public-appointment-code">Mã lịch hẹn <b>*</b></label>
              <Input
                ref={appointmentCodeInput}
                id="public-appointment-code"
                name="appointmentCode"
                size="large"
                prefix={<CalendarOutlined />}
                value={appointmentCode}
                maxLength={20}
                placeholder="Ví dụ: LH-7F2A91C4D8BE"
                autoComplete="off"
                disabled={loading}
                required
                aria-required="true"
                status={fieldErrors.appointmentCode ? 'error' : undefined}
                aria-invalid={!!fieldErrors.appointmentCode}
                aria-describedby={fieldErrors.appointmentCode ? 'appointment-code-error' : 'appointment-code-hint'}
                onChange={handleCodeChange}
              />
              {fieldErrors.appointmentCode
                ? <small className="public-lookup-field-error" id="appointment-code-error">{fieldErrors.appointmentCode}</small>
                : <small id="appointment-code-hint">Không chứa khoảng trắng hoặc ký tự đặc biệt.</small>}
            </div>

            <div className="public-lookup-field">
              <label htmlFor="public-date-of-birth">Ngày sinh đã đăng ký <b>*</b></label>
              <DatePicker
                ref={dateOfBirthInput}
                id="public-date-of-birth"
                size="large"
                value={dateOfBirth}
                format="DD/MM/YYYY"
                placeholder="Chọn ngày sinh"
                inputReadOnly
                disabled={loading}
                required
                aria-required="true"
                disabledDate={(date) => date && date.isAfter(dayjs(), 'day')}
                status={fieldErrors.dateOfBirth ? 'error' : undefined}
                aria-invalid={!!fieldErrors.dateOfBirth}
                aria-describedby={fieldErrors.dateOfBirth ? 'date-of-birth-error' : 'date-of-birth-hint'}
                onChange={handleDateChange}
              />
              {fieldErrors.dateOfBirth
                ? <small className="public-lookup-field-error" id="date-of-birth-error">{fieldErrors.dateOfBirth}</small>
                : <small id="date-of-birth-hint">Dùng để xác minh, không hiển thị trong kết quả.</small>}
            </div>

            <Button
              className="public-lookup-submit"
              type="primary"
              size="large"
              htmlType="submit"
              icon={<SearchOutlined />}
              loading={loading}
              disabled={loading}
              block
            >
              Tra cứu lịch hẹn
            </Button>

            <div className="public-lookup-privacy"><LockOutlined /> Dữ liệu tra cứu không được lưu trên trình duyệt.</div>
          </form>

          <div className="public-lookup-feedback" aria-live="polite">
            {errorMessage && (
              <Alert
                type="warning"
                showIcon
                closable
                message="Không thể tra cứu lịch hẹn"
                description={errorMessage}
                onClose={() => setErrorMessage('')}
              />
            )}

            {lookupResult && status && scheduledAt && (
              <article className="public-lookup-result">
                <header>
                  <span className={`public-lookup-result-icon public-lookup-result-${status.tone}`}>
                    {getStatusIcon(lookupResult.careState)}
                  </span>
                  <div>
                    <small>Kết quả tra cứu</small>
                    <h3>{lookupResult.appointmentCode}</h3>
                  </div>
                  <span className={`public-lookup-status public-lookup-status-${status.tone}`}>
                    {status.label}
                  </span>
                </header>

                <div className="public-lookup-result-grid">
                  <div><span><CalendarOutlined /></span><p><small>Ngày hẹn</small><strong>{scheduledAt.format('DD/MM/YYYY')}</strong></p></div>
                  <div><span><ClockCircleOutlined /></span><p><small>Giờ hẹn</small><strong>{scheduledAt.format('HH:mm')}</strong></p></div>
                </div>

                <div className={`public-lookup-result-note public-lookup-result-note-${status.tone}`}>
                  <InfoCircleOutlined />
                  <p>{status.description}</p>
                </div>

                <div className="public-lookup-result-actions">
                  <Button icon={<ArrowLeftOutlined />} onClick={resetLookup}>Tra cứu lịch khác</Button>
                </div>
              </article>
            )}
          </div>
        </section>
      </main>

      <footer className="public-lookup-footer">
        <span>© {new Date().getFullYear()} Bệnh Án Số</span>
        <span><SafetyCertificateOutlined /> Kết nối được bảo vệ</span>
      </footer>
    </div>
  )
}

export default PublicLookupPage
