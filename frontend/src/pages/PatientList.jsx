import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Table, Button, Input, Space, Tag, Typography, Modal, Form, Select, DatePicker, message } from 'antd'
import { PlusOutlined, EyeOutlined } from '@ant-design/icons'
import { formatDate, formatGender } from '../utils/helpers'
import { getPatients } from '../services/mockDataService'

const { Title } = Typography

function PatientList() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [patients, setPatients] = useState([])
  const [keyword, setKeyword] = useState('')
  const [searchText, setSearchText] = useState('')

  const [registerOpen, setRegisterOpen] = useState(false)
  const [registerForm] = Form.useForm()

  useEffect(() => {
    setLoading(true)
    const timer = setTimeout(() => {
      setPatients(getPatients(keyword))
      setLoading(false)
    }, 200)

    return () => clearTimeout(timer)
  }, [keyword])

  const handleSearch = () => {
    setKeyword(searchText)
  }

  // --- NCL-02-CN-001: Đăng ký hồ sơ bệnh nhân mới ---
  const openRegister = () => {
    registerForm.resetFields()
    if (searchText) registerForm.setFieldsValue({ fullName: searchText })
    setRegisterOpen(true)
  }

  const handleRegister = (values) => {
    // Kiểm tra trùng thông tin định danh (họ tên + ngày sinh) trong danh sách hiện có
    const dobStr = values.dateOfBirth.format('YYYY-MM-DD')
    const duplicate = patients.find(
      (p) =>
        p.fullName.trim().toLowerCase() === values.fullName.trim().toLowerCase() &&
        formatDate(p.dateOfBirth) === values.dateOfBirth.format(formatDate(p.dateOfBirth).includes('/') ? 'DD/MM/YYYY' : 'YYYY-MM-DD')
    )
    if (duplicate) {
      message.warning(`Đã tồn tại hồ sơ trùng thông tin định danh (mã ${duplicate.patientCode}). Vui lòng tra cứu trước khi tạo mới.`)
      return
    }

    // TODO: thay bằng gọi API/service thật, ví dụ addPatient(values), khi có backend.
    // Hiện tại addPatient chưa có trong mockDataService — cần bổ sung hàm này để lưu lại giữa các lần tìm kiếm.
    const newPatient = {
      id: `tmp-${Date.now()}`,
      patientCode: `BN-${Math.floor(1000 + Math.random() * 9000)}`,
      fullName: values.fullName,
      dateOfBirth: dobStr,
      gender: values.gender,
      phoneNumber: values.phoneNumber,
      healthInsuranceCode: values.healthInsuranceCode || '',
    }
    setPatients((prev) => [newPatient, ...prev])
    message.success(`Tạo hồ sơ thành công, mã bệnh nhân: ${newPatient.patientCode}`)
    setRegisterOpen(false)
  }

  const columns = [
    {
      title: 'Mã BN',
      dataIndex: 'patientCode',
      key: 'patientCode',
      width: 120,
      render: (text) => <Tag color="blue">{text}</Tag>,
    },
    {
      title: 'Họ tên',
      dataIndex: 'fullName',
      key: 'fullName',
      ellipsis: true,
    },
    {
      title: 'Ngày sinh',
      dataIndex: 'dateOfBirth',
      key: 'dateOfBirth',
      width: 120,
      render: (date) => formatDate(date),
    },
    {
      title: 'Giới tính',
      dataIndex: 'gender',
      key: 'gender',
      width: 100,
      render: (gender) => formatGender(gender),
    },
    {
      title: 'Số điện thoại',
      dataIndex: 'phoneNumber',
      key: 'phoneNumber',
      width: 140,
    },
    {
      title: 'BHYT',
      dataIndex: 'healthInsuranceCode',
      key: 'healthInsuranceCode',
      width: 140,
      ellipsis: true,
    },
    {
      title: 'Thao tác',
      key: 'actions',
      width: 100,
      render: (_, record) => (
        <Button
          type="link"
          icon={<EyeOutlined />}
          onClick={() => navigate(`/patients/${record.id}`)}
        >
          Xem
        </Button>
      ),
    },
  ]

  return (
    <div>
      <div className="page-header">
        <Title level={4} style={{ margin: 0 }}>Quản lý bệnh nhân</Title>
        <Space>
          <Input.Search
            placeholder="Tìm kiếm bệnh nhân..."
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onSearch={handleSearch}
            enterButton
            style={{ width: 300 }}
          />
          <Button type="primary" icon={<PlusOutlined />} onClick={openRegister}>
            Thêm bệnh nhân
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={patients}
        rowKey="id"
        loading={loading}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showTotal: (total) => `Tổng số: ${total} bệnh nhân`,
        }}
      />

      <Modal
        title="Đăng ký hồ sơ bệnh nhân mới"
        open={registerOpen}
        onCancel={() => setRegisterOpen(false)}
        onOk={() => registerForm.submit()}
        okText="Lưu hồ sơ"
        cancelText="Hủy"
      >
        <Form form={registerForm} layout="vertical" onFinish={handleRegister}>
          <Form.Item
            name="fullName"
            label="Họ và tên"
            rules={[{ required: true, message: 'Vui lòng nhập họ và tên' }]}
          >
            <Input placeholder="Nguyễn Văn A" />
          </Form.Item>

          <Form.Item
            name="dateOfBirth"
            label="Ngày sinh"
            rules={[{ required: true, message: 'Vui lòng chọn ngày sinh' }]}
          >
            <DatePicker format="DD/MM/YYYY" style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item
            name="gender"
            label="Giới tính"
            rules={[{ required: true, message: 'Vui lòng chọn giới tính' }]}
          >
            <Select
              placeholder="Chọn giới tính"
              options={[
                { value: 'male', label: 'Nam' },
                { value: 'female', label: 'Nữ' },
                { value: 'other', label: 'Khác' },
              ]}
            />
          </Form.Item>

          <Form.Item
            name="phoneNumber"
            label="Số điện thoại liên hệ"
            rules={[
              { required: true, message: 'Vui lòng nhập số điện thoại' },
              { pattern: /^0\d{9}$/, message: 'Số điện thoại không đúng định dạng' },
            ]}
          >
            <Input placeholder="09xxxxxxxx" />
          </Form.Item>

          <Form.Item name="healthInsuranceCode" label="Mã BHYT (nếu có)">
            <Input placeholder="Không bắt buộc" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default PatientList