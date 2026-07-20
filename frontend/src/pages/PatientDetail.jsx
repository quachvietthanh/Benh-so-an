import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Card, Descriptions, Tag, Button, Spin, Space, Typography, Divider, Table } from 'antd'
import { ArrowLeftOutlined, EditOutlined } from '@ant-design/icons'
import patientApi from '../api/patientApi'
import medicalRecordApi from '../api/medicalRecordApi'
import { formatDate, formatDateTime, formatGender, formatRecordStatus } from '../utils/helpers'

const { Title } = Typography

function PatientDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(true)
  const [patient, setPatient] = useState(null)
  const [records, setRecords] = useState([])

  useEffect(() => {
    const fetchData = async () => {
      try {
        const patientRes = await patientApi.getById(id)
        setPatient(patientRes.data)

        const recordsRes = await medicalRecordApi.getByPatient(id, { page: 0, size: 10 })
        setRecords(recordsRes.data.content)
      } catch (error) {
        console.error('Failed to fetch patient:', error)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [id])

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', paddingTop: 100 }}>
        <Spin size="large" />
      </div>
    )
  }

  if (!patient) {
    return <div>Không tìm thấy bệnh nhân</div>
  }

  const recordColumns = [
    {
      title: 'Mã hồ sơ',
      dataIndex: 'recordCode',
      key: 'recordCode',
      render: (text) => <Tag color="green">{text}</Tag>,
    },
    {
      title: 'Chẩn đoán',
      dataIndex: 'diagnosis',
      key: 'diagnosis',
      ellipsis: true,
    },
    {
      title: 'Bác sĩ',
      dataIndex: 'doctorName',
      key: 'doctorName',
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      render: (status) => {
        const formatted = formatRecordStatus(status)
        return <Tag color={formatted.color}>{formatted.label}</Tag>
      },
    },
    {
      title: 'Ngày tạo',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date) => formatDateTime(date),
    },
  ]

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button
          icon={<ArrowLeftOutlined />}
          onClick={() => navigate('/patients')}
        >
          Quay lại
        </Button>
      </Space>

      <Card
        title={
          <Space>
            <Title level={5} style={{ margin: 0 }}>Thông tin bệnh nhân</Title>
            <Tag color="blue">{patient.patientCode}</Tag>
          </Space>
        }
        extra={<Button type="primary" icon={<EditOutlined />}>Chỉnh sửa</Button>}
        style={{ borderRadius: 12, marginBottom: 24 }}
      >
        <Descriptions bordered column={2}>
          <Descriptions.Item label="Họ tên" span={2}>
            {patient.fullName}
          </Descriptions.Item>
          <Descriptions.Item label="Ngày sinh">
            {formatDate(patient.dateOfBirth)}
          </Descriptions.Item>
          <Descriptions.Item label="Giới tính">
            {formatGender(patient.gender)}
          </Descriptions.Item>
          <Descriptions.Item label="Số điện thoại">
            {patient.phoneNumber || '---'}
          </Descriptions.Item>
          <Descriptions.Item label="Email">
            {patient.email || '---'}
          </Descriptions.Item>
          <Descriptions.Item label="Địa chỉ" span={2}>
            {patient.address || '---'}
          </Descriptions.Item>
          <Descriptions.Item label="Số CMND/CCCD">
            {patient.identityNumber || '---'}
          </Descriptions.Item>
          <Descriptions.Item label="Mã BHYT">
            {patient.healthInsuranceCode || '---'}
          </Descriptions.Item>
          <Descriptions.Item label="Nhóm máu">
            <Tag>{patient.bloodType || '---'}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Người liên hệ khẩn cấp">
            {patient.emergencyContact || '---'}
          </Descriptions.Item>
          <Descriptions.Item label="Tiền sử bệnh" span={2}>
            {patient.medicalHistory || 'Không có'}
          </Descriptions.Item>
          <Descriptions.Item label="Dị ứng" span={2}>
            {patient.allergies || 'Không có'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="Hồ sơ bệnh án" style={{ borderRadius: 12 }}>
        <Table
          columns={recordColumns}
          dataSource={records}
          rowKey="id"
          pagination={false}
        />
      </Card>
    </div>
  )
}

export default PatientDetail
