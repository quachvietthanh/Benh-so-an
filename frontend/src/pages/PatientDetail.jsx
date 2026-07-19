import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Card, Descriptions, Tag, Button, Spin, Space, Typography, Table, Modal, Form, Input, Select, DatePicker, message, List } from 'antd'
import { ArrowLeftOutlined, EditOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { formatDate, formatDateTime, formatGender, formatRecordStatus } from '../utils/helpers'
import { getPatientById, getMedicalRecords } from '../services/mockDataService'

const { Title } = Typography

const phoneRegex = /^0\d{9}$/

function PatientDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(true)
  const [patient, setPatient] = useState(null)
  const [records, setRecords] = useState([])

  const [editOpen, setEditOpen] = useState(false)
  const [editForm] = Form.useForm()
  const [editHistory, setEditHistory] = useState([])

  useEffect(() => {
    const fetchData = () => {
      const patientData = getPatientById(id)
      setPatient(patientData)
      setRecords(getMedicalRecords().filter((record) => record.patientId === id))
      setLoading(false)
    }

    fetchData()
  }, [id])

  // --- NCL-02-CN-003: Sửa toàn bộ thông tin hồ sơ ---
  const openEdit = () => {
    editForm.setFieldsValue({
      fullName: patient.fullName,
      dateOfBirth: patient.dateOfBirth ? dayjs(patient.dateOfBirth) : null,
      gender: patient.gender,
      phoneNumber: patient.phoneNumber,
      email: patient.email,
      address: patient.address,
      identityNumber: patient.identityNumber,
      healthInsuranceCode: patient.healthInsuranceCode,
      bloodType: patient.bloodType,
      emergencyContact: patient.emergencyContact,
      medicalHistory: patient.medicalHistory,
      allergies: patient.allergies,
    })
    setEditOpen(true)
  }

  const handleEdit = (values) => {
    if (!phoneRegex.test(values.phoneNumber)) {
      message.error('Số điện thoại không đúng định dạng (phải bắt đầu bằng 0 và đủ 10 số)')
      return
    }

    const normalizedValues = {
      ...values,
      dateOfBirth: values.dateOfBirth ? values.dateOfBirth.format('YYYY-MM-DD') : patient.dateOfBirth,
    }

    // Ghi nhận những trường thay đổi so với dữ liệu cũ
    const changedFields = Object.keys(normalizedValues).filter((key) => normalizedValues[key] !== patient[key])

    // TODO: thay bằng gọi API/service thật để lưu lại thay đổi và ghi log phía backend.
    setPatient((prev) => ({ ...prev, ...normalizedValues }))

    if (changedFields.length > 0) {
      setEditHistory((prev) => [
        {
          time: new Date().toLocaleString('vi-VN'),
          fields: changedFields,
        },
        ...prev,
      ])
    }

    message.success('Đã lưu thay đổi thông tin bệnh nhân')
    setEditOpen(false)
  }

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
        extra={<Button type="primary" icon={<EditOutlined />} onClick={openEdit}>Chỉnh sửa</Button>}
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

      <Card title="Hồ sơ bệnh án" style={{ borderRadius: 12, marginBottom: 24 }}>
        <Table
          columns={recordColumns}
          dataSource={records}
          rowKey="id"
          pagination={false}
        />
      </Card>

      {editHistory.length > 0 && (
        <Card title="Lịch sử chỉnh sửa thông tin liên hệ" style={{ borderRadius: 12 }}>
          <List
            dataSource={editHistory}
            renderItem={(item) => (
              <List.Item>
                <span>{item.time} — đã cập nhật: {item.fields.join(', ')}</span>
              </List.Item>
            )}
          />
        </Card>
      )}

      <Modal
        title="Chỉnh sửa thông tin bệnh nhân"
        open={editOpen}
        onCancel={() => setEditOpen(false)}
        onOk={() => editForm.submit()}
        okText="Lưu thay đổi"
        cancelText="Hủy"
        width={640}
      >
        <Form form={editForm} layout="vertical" onFinish={handleEdit}>
          <Form.Item
            name="fullName"
            label="Họ tên"
            rules={[{ required: true, message: 'Vui lòng nhập họ tên' }]}
          >
            <Input />
          </Form.Item>

          <Space.Compact block>
            <Form.Item
              name="dateOfBirth"
              label="Ngày sinh"
              style={{ width: '100%', marginRight: 12 }}
              rules={[{ required: true, message: 'Chọn ngày sinh' }]}
            >
              <DatePicker format="DD/MM/YYYY" style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="gender" label="Giới tính" style={{ width: '100%' }}>
              <Select
                options={[
                  { value: 'male', label: 'Nam' },
                  { value: 'female', label: 'Nữ' },
                  { value: 'other', label: 'Khác' },
                ]}
              />
            </Form.Item>
          </Space.Compact>

          <Space.Compact block>
            <Form.Item
              name="phoneNumber"
              label="Số điện thoại"
              style={{ width: '100%', marginRight: 12 }}
              rules={[
                { required: true, message: 'Vui lòng nhập số điện thoại' },
                { pattern: phoneRegex, message: 'Số điện thoại không đúng định dạng' },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="email"
              label="Email"
              style={{ width: '100%' }}
              rules={[{ type: 'email', message: 'Email không đúng định dạng' }]}
            >
              <Input />
            </Form.Item>
          </Space.Compact>

          <Form.Item name="address" label="Địa chỉ">
            <Input />
          </Form.Item>

          <Space.Compact block>
            <Form.Item name="identityNumber" label="Số CMND/CCCD" style={{ width: '100%', marginRight: 12 }}>
              <Input />
            </Form.Item>
            <Form.Item name="healthInsuranceCode" label="Mã BHYT" style={{ width: '100%' }}>
              <Input />
            </Form.Item>
          </Space.Compact>

          <Space.Compact block>
            <Form.Item name="bloodType" label="Nhóm máu" style={{ width: '100%', marginRight: 12 }}>
              <Select
                allowClear
                options={['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'].map((v) => ({ value: v, label: v }))}
              />
            </Form.Item>
            <Form.Item name="emergencyContact" label="Người liên hệ khẩn cấp" style={{ width: '100%' }}>
              <Input placeholder="Họ tên - số điện thoại" />
            </Form.Item>
          </Space.Compact>

          <Form.Item name="medicalHistory" label="Tiền sử bệnh">
            <Input.TextArea rows={2} />
          </Form.Item>

          <Form.Item name="allergies" label="Dị ứng">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default PatientDetail