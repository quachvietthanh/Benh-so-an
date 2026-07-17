import React, { useState } from 'react'
import {
  Card, Table, Tag, Button, Typography, Alert, Modal, Form,
  Input, Select, Space, Switch, Popconfirm, Badge, Tooltip
} from 'antd'
import {
  PlusOutlined, LockOutlined, UnlockOutlined, EditOutlined, UserOutlined, TeamOutlined
} from '@ant-design/icons'
import { getUsers, createUser, toggleUserLock, updateUserRoles } from '../services/mockDataService'

const { Title, Text } = Typography

const ALL_ROLES = [
  { value: 'admin', label: 'Quản trị viên' },
  { value: 'manager', label: 'Quản lý phòng khám' },
  { value: 'doctor', label: 'Bác sĩ' },
  { value: 'receptionist', label: 'Lễ tân' },
  { value: 'pharmacist', label: 'Dược sĩ' },
]

const roleColor = {
  admin: 'red',
  manager: 'purple',
  doctor: 'blue',
  receptionist: 'cyan',
  pharmacist: 'green',
}

const roleLabel = {
  admin: 'Quản trị viên',
  manager: 'Quản lý',
  doctor: 'Bác sĩ',
  receptionist: 'Lễ tân',
  pharmacist: 'Dược sĩ',
}

function UsersPage() {
  const [users, setUsers] = useState(getUsers())
  const [createVisible, setCreateVisible] = useState(false)
  const [editRolesVisible, setEditRolesVisible] = useState(false)
  const [selectedUser, setSelectedUser] = useState(null)
  const [formCreate] = Form.useForm()
  const [formRoles] = Form.useForm()

  const refresh = () => setUsers(getUsers())

  const handleCreate = () => {
    formCreate.validateFields().then((vals) => {
      createUser(vals)
      refresh()
      setCreateVisible(false)
      formCreate.resetFields()
    })
  }

  const handleToggleLock = (id) => {
    toggleUserLock(id)
    refresh()
  }

  const openEditRoles = (user) => {
    setSelectedUser(user)
    formRoles.setFieldsValue({ roles: user.roles })
    setEditRolesVisible(true)
  }

  const handleSaveRoles = () => {
    formRoles.validateFields().then((vals) => {
      updateUserRoles(selectedUser.id, vals.roles)
      refresh()
      setEditRolesVisible(false)
    })
  }

  const columns = [
    {
      title: 'Người dùng',
      key: 'info',
      render: (_, rec) => (
        <Space>
          <div style={{
            width: 36, height: 36, borderRadius: '50%',
            background: rec.isLocked ? '#94a3b8' : '#0ea5e9',
            color: 'white', display: 'flex', alignItems: 'center',
            justifyContent: 'center', fontWeight: 700, fontSize: 14,
          }}>
            {rec.fullName.charAt(0)}
          </div>
          <div>
            <div style={{ fontWeight: 600 }}>{rec.fullName}</div>
            <Text type="secondary" style={{ fontSize: 12 }}>{rec.username}</Text>
          </div>
        </Space>
      ),
    },
    { title: 'Email', dataIndex: 'email', key: 'email' },
    {
      title: 'Vai trò',
      dataIndex: 'roles',
      key: 'roles',
      render: (roles) => roles.map((role) => (
        <Tag key={role} color={roleColor[role] || 'default'}>{roleLabel[role] || role}</Tag>
      )),
    },
    { title: 'Ngày tạo', dataIndex: 'createdAt', key: 'createdAt' },
    {
      title: 'Trạng thái',
      dataIndex: 'isLocked',
      key: 'isLocked',
      render: (locked) => locked
        ? <Badge status="error" text="Đã khóa" />
        : <Badge status="success" text="Hoạt động" />,
    },
    {
      title: 'Thao tác',
      key: 'actions',
      render: (_, record) => (
        <Space>
          <Tooltip title="Chỉnh sửa vai trò">
            <Button
              size="small"
              icon={<EditOutlined />}
              onClick={() => openEditRoles(record)}
            >
              Vai trò
            </Button>
          </Tooltip>
          <Popconfirm
            title={record.isLocked ? 'Mở khóa tài khoản này?' : 'Khóa tài khoản này?'}
            onConfirm={() => handleToggleLock(record.id)}
            okText="Xác nhận"
            cancelText="Hủy"
            okButtonProps={{ danger: !record.isLocked }}
          >
            <Button
              size="small"
              danger={!record.isLocked}
              icon={record.isLocked ? <UnlockOutlined /> : <LockOutlined />}
            >
              {record.isLocked ? 'Mở khóa' : 'Khóa'}
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <div className="page-header">
        <div>
          <Title className="page-title" level={3}>Quản trị tài khoản hệ thống</Title>
          <Text type="secondary">Tạo, khóa/mở và phân quyền tài khoản người dùng (NCL-09)</Text>
        </div>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={() => setCreateVisible(true)}>
          Tạo tài khoản mới
        </Button>
      </div>

      <Alert
        type="warning"
        showIcon
        message="Chỉ quản trị viên mới có quyền tạo, khóa/mở tài khoản và thay đổi vai trò. Mọi thay đổi đều được ghi nhật ký."
        style={{ marginBottom: 16 }}
      />

      <Card
        title={<Space><TeamOutlined style={{ color: '#6366f1' }} /><span>Danh sách người dùng ({users.length})</span></Space>}
        style={{ borderRadius: 12 }}
      >
        <Table
          columns={columns}
          dataSource={users}
          rowKey="id"
          pagination={{ pageSize: 8 }}
          rowClassName={(rec) => rec.isLocked ? 'row-locked' : ''}
        />
      </Card>

      {/* Modal tạo tài khoản */}
      <Modal
        title={<Space><UserOutlined style={{ color: '#0ea5e9' }} /><span>Tạo tài khoản mới</span></Space>}
        open={createVisible}
        onCancel={() => { setCreateVisible(false); formCreate.resetFields() }}
        onOk={handleCreate}
        okText="Tạo tài khoản"
        cancelText="Hủy"
        destroyOnClose
        width={520}
      >
        <Form form={formCreate} layout="vertical">
          <Form.Item label="Họ và tên" name="fullName" rules={[{ required: true, message: 'Nhập họ tên' }]}>
            <Input placeholder="Nhập họ và tên đầy đủ" prefix={<UserOutlined />} />
          </Form.Item>
          <Form.Item label="Tên đăng nhập" name="username" rules={[{ required: true, message: 'Nhập tên đăng nhập' }]}>
            <Input placeholder="Nhập username" />
          </Form.Item>
          <Form.Item label="Mật khẩu" name="password" rules={[{ required: true, message: 'Nhập mật khẩu' }, { min: 6, message: 'Ít nhất 6 ký tự' }]}>
            <Input.Password placeholder="Nhập mật khẩu" />
          </Form.Item>
          <Form.Item label="Email" name="email" rules={[{ type: 'email', message: 'Email không hợp lệ' }]}>
            <Input placeholder="email@benhsoan.vn" />
          </Form.Item>
          <Form.Item label="Vai trò" name="roles" rules={[{ required: true, message: 'Chọn ít nhất một vai trò' }]}>
            <Select mode="multiple" placeholder="Chọn vai trò" options={ALL_ROLES} />
          </Form.Item>
        </Form>
      </Modal>

      {/* Modal chỉnh sửa vai trò */}
      <Modal
        title={<Space><EditOutlined /><span>Chỉnh sửa vai trò: {selectedUser?.fullName}</span></Space>}
        open={editRolesVisible}
        onCancel={() => setEditRolesVisible(false)}
        onOk={handleSaveRoles}
        okText="Lưu vai trò"
        cancelText="Hủy"
        destroyOnClose
        width={420}
      >
        <Alert type="info" showIcon message="Thay đổi vai trò có hiệu lực ngay lần đăng nhập tiếp theo." style={{ marginBottom: 16 }} />
        <Form form={formRoles} layout="vertical">
          <Form.Item label="Vai trò" name="roles" rules={[{ required: true, message: 'Chọn ít nhất một vai trò' }]}>
            <Select mode="multiple" placeholder="Chọn vai trò" options={ALL_ROLES} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default UsersPage
