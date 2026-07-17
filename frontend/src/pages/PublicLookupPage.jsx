import React, { useState } from 'react'
import { Card, Input, Button, Alert, Typography, Tag, Divider, Space, Spin, Badge } from 'antd'
import {
  SearchOutlined, CalendarOutlined, UserOutlined,
  CheckCircleOutlined, ClockCircleOutlined, CloseCircleOutlined, MedicineBoxOutlined
} from '@ant-design/icons'
import { lookupByAppointmentCode } from '../services/mockDataService'

const { Title, Text } = Typography

const statusConfig = {
  SCHEDULED: { color: 'blue', icon: <ClockCircleOutlined />, label: 'Đã lên lịch' },
  CHECKED_IN: { color: 'orange', icon: <CalendarOutlined />, label: 'Đang chờ khám' },
  COMPLETED: { color: 'green', icon: <CheckCircleOutlined />, label: 'Đã hoàn thành' },
  NO_SHOW: { color: 'red', icon: <CloseCircleOutlined />, label: 'Vắng mặt' },
}

function PublicLookupPage() {
  const [code, setCode] = useState('')
  const [result, setResult] = useState(null)
  const [notFound, setNotFound] = useState(false)
  const [loading, setLoading] = useState(false)

  const handleSearch = () => {
    if (!code.trim()) return
    setLoading(true)
    setNotFound(false)
    setResult(null)
    // Simulate async
    setTimeout(() => {
      const found = lookupByAppointmentCode(code)
      if (found) {
        setResult(found)
      } else {
        setNotFound(true)
      }
      setLoading(false)
    }, 500)
  }

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleSearch()
  }

  const apptStatus = result?.appointment?.status
  const statusCfg = statusConfig[apptStatus] || {}

  return (
    <div style={{ maxWidth: 680, margin: '0 auto' }}>
      <div className="page-header">
        <div>
          <Title className="page-title" level={3}>Cổng tra cứu kết quả khám</Title>
          <Text type="secondary">Tra cứu thông tin lịch hẹn và kết quả khám theo mã hẹn (NCL-10)</Text>
        </div>
      </div>

      <Alert
        type="info"
        showIcon
        message="Chỉ hiển thị kết quả của mã hẹn hợp lệ. Dữ liệu bệnh nhân khác không được tiết lộ."
        style={{ marginBottom: 24 }}
      />

      <Card style={{ borderRadius: 16, boxShadow: '0 4px 24px rgba(0,0,0,0.07)' }}>
        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <SearchOutlined style={{ fontSize: 48, color: '#0ea5e9' }} />
          <Title level={4} style={{ margin: '12px 0 4px' }}>Nhập mã hẹn để tra cứu</Title>
          <Text type="secondary">Mã hẹn được cung cấp khi đăng ký lịch hẹn (VD: HEN-001)</Text>
        </div>

        <Input.Search
          size="large"
          placeholder="Nhập mã hẹn... VD: HEN-001, HEN-002"
          value={code}
          onChange={(e) => setCode(e.target.value.toUpperCase())}
          onKeyDown={handleKeyDown}
          onSearch={handleSearch}
          enterButton={<Button type="primary" icon={<SearchOutlined />}>Tra cứu</Button>}
          style={{ marginBottom: 8 }}
        />
        <Text type="secondary" style={{ fontSize: 12 }}>
          Thử các mã mẫu: <Text code>HEN-001</Text>, <Text code>HEN-002</Text>, <Text code>HEN-003</Text>, <Text code>HEN-004</Text>
        </Text>
      </Card>

      {loading && (
        <Card style={{ borderRadius: 12, marginTop: 16, textAlign: 'center' }}>
          <Spin size="large" />
          <div style={{ marginTop: 12 }}>Đang tra cứu...</div>
        </Card>
      )}

      {notFound && !loading && (
        <Card style={{ borderRadius: 12, marginTop: 16 }}>
          <Alert
            type="error"
            showIcon
            icon={<CloseCircleOutlined />}
            message="Không tìm thấy mã hẹn"
            description={`Mã hẹn "${code}" không tồn tại trong hệ thống. Vui lòng kiểm tra lại.`}
          />
        </Card>
      )}

      {result && !loading && (
        <Card
          style={{ borderRadius: 16, marginTop: 16, boxShadow: '0 4px 24px rgba(0,0,0,0.07)' }}
          title={
            <Space>
              <CalendarOutlined style={{ color: '#0ea5e9' }} />
              <span>Thông tin lịch hẹn — {result.appointment.appointmentCode}</span>
            </Space>
          }
        >
          <Alert
            type={apptStatus === 'COMPLETED' ? 'success' : apptStatus === 'NO_SHOW' ? 'error' : 'info'}
            showIcon
            icon={statusCfg.icon}
            message={
              <Space>
                <span>Trạng thái:</span>
                <Tag color={statusCfg.color} style={{ margin: 0 }}>{statusCfg.label}</Tag>
              </Space>
            }
            style={{ marginBottom: 16 }}
          />

          {/* Appointment info */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px 24px' }}>
            <div>
              <Text type="secondary" style={{ fontSize: 12, display: 'block' }}>Mã hẹn</Text>
              <Text strong style={{ fontSize: 15 }}>{result.appointment.appointmentCode}</Text>
            </div>
            <div>
              <Text type="secondary" style={{ fontSize: 12, display: 'block' }}>Ngày khám</Text>
              <Text strong>{result.appointment.date}</Text>
            </div>
            <div>
              <Text type="secondary" style={{ fontSize: 12, display: 'block' }}>Giờ khám</Text>
              <Text strong>{result.appointment.slot}</Text>
            </div>
            <div>
              <Text type="secondary" style={{ fontSize: 12, display: 'block' }}>Bác sĩ</Text>
              <Text strong>{result.appointment.doctorName}</Text>
            </div>
          </div>

          {result.patient && (
            <>
              <Divider>
                <Space><UserOutlined /><span>Thông tin bệnh nhân</span></Space>
              </Divider>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px 24px' }}>
                <div>
                  <Text type="secondary" style={{ fontSize: 12, display: 'block' }}>Họ tên</Text>
                  <Text strong>{result.patient.fullName}</Text>
                </div>
                <div>
                  <Text type="secondary" style={{ fontSize: 12, display: 'block' }}>Mã bệnh nhân</Text>
                  <Text strong>{result.patient.patientCode}</Text>
                </div>
                <div>
                  <Text type="secondary" style={{ fontSize: 12, display: 'block' }}>Ngày sinh</Text>
                  <Text strong>{result.patient.dateOfBirth}</Text>
                </div>
              </div>
            </>
          )}

          {result.record && apptStatus === 'COMPLETED' && (
            <>
              <Divider>
                <Space><MedicineBoxOutlined /><span>Kết quả khám</span></Space>
              </Divider>
              <div style={{ background: '#f0fdf4', borderRadius: 8, padding: 16 }}>
                <div style={{ marginBottom: 12 }}>
                  <Text type="secondary" style={{ fontSize: 12, display: 'block' }}>Chẩn đoán</Text>
                  <Text strong style={{ fontSize: 15 }}>{result.record.diagnosis}</Text>
                </div>
                <div style={{ marginBottom: 12 }}>
                  <Text type="secondary" style={{ fontSize: 12, display: 'block' }}>Ngày lập bệnh án</Text>
                  <Text strong>{new Date(result.record.createdAt).toLocaleString('vi-VN')}</Text>
                </div>
                {result.record.followUpDate && (
                  <div style={{ background: '#fff7ed', borderRadius: 6, padding: '8px 12px', border: '1px solid #fed7aa' }}>
                    <CalendarOutlined style={{ color: '#f59e0b', marginRight: 8 }} />
                    <Text strong style={{ color: '#92400e' }}>Ngày tái khám: {result.record.followUpDate}</Text>
                  </div>
                )}
              </div>
            </>
          )}
        </Card>
      )}
    </div>
  )
}

export default PublicLookupPage
