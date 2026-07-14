import { useEffect, useState } from 'react'
import {
  Table,
  Button,
  Tag,
  Modal,
  Form,
  Input,
  Select,
  Space,
  message,
  Typography,
  Popconfirm,
} from 'antd'
import { PlusOutlined, LockOutlined, UnlockOutlined } from '@ant-design/icons'
import accountApi from '../../api/accountApi'
import { ROLES, ROLE_LABELS, ACCOUNT_STATUS } from '../../utils/constants'

export default function AccountManagementPage() {
  const [accounts, setAccounts] = useState([])
  const [loading, setLoading] = useState(false)
  const [createOpen, setCreateOpen] = useState(false)
  const [creating, setCreating] = useState(false)
  const [form] = Form.useForm()

  const fetchAccounts = async () => {
    setLoading(true)
    try {
      const res = await accountApi.getAll()
      setAccounts(res.data)
    } catch {
      message.error('Không tải được danh sách tài khoản.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchAccounts()
  }, [])

  // NCL-01-CN-002-TC-01/02/03: tạo tài khoản, báo trùng, yêu cầu bổ sung vai trò
  const handleCreate = async (values) => {
    setCreating(true)
    try {
      await accountApi.create(values)
      message.success('Tạo tài khoản thành công.')
      setCreateOpen(false)
      form.resetFields()
      fetchAccounts()
    } catch (err) {
      if (err.response?.status === 409) {
        form.setFields([{ name: 'username', errors: ['Tên tài khoản đã tồn tại.'] }])
      } else {
        message.error('Tạo tài khoản thất bại.')
      }
    } finally {
      setCreating(false)
    }
  }

  // NCL-01-CN-004-TC-01/02/03: khóa/mở tài khoản kèm lý do
  const handleToggleStatus = async (record) => {
    const nextStatus =
      record.status === ACCOUNT_STATUS.ACTIVE ? ACCOUNT_STATUS.LOCKED : ACCOUNT_STATUS.ACTIVE
    try {
      await accountApi.updateStatus(record.id, nextStatus, 'Thao tác từ trang quản lý tài khoản')
      message.success(
        nextStatus === ACCOUNT_STATUS.LOCKED ? 'Đã khóa tài khoản.' : 'Đã mở khóa tài khoản.',
      )
      fetchAccounts()
    } catch {
      message.error('Thao tác thất bại, vui lòng thử lại.')
    }
  }

  // NCL-01-CN-003-TC-01: gán vai trò cho tài khoản
  const handleRoleChange = async (record, roleId) => {
    try {
      await accountApi.updateRole(record.id, roleId)
      message.success('Đã cập nhật vai trò.')
      fetchAccounts()
    } catch {
      message.error('Cập nhật vai trò thất bại.')
    }
  }

  const columns = [
    { title: 'Họ tên', dataIndex: 'fullName' },
    { title: 'Tài khoản', dataIndex: 'username' },
    {
      title: 'Vai trò',
      dataIndex: 'role',
      render: (role, record) => (
        <Select
          value={role}
          style={{ width: 160 }}
          options={Object.values(ROLES).map((r) => ({ value: r, label: ROLE_LABELS[r] }))}
          onChange={(value) => handleRoleChange(record, value)}
        />
      ),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      render: (status) =>
        status === ACCOUNT_STATUS.ACTIVE ? (
          <Tag color="green">Đang hoạt động</Tag>
        ) : (
          <Tag color="red">Đã khóa</Tag>
        ),
    },
    {
      title: 'Thao tác',
      render: (_, record) => (
        <Popconfirm
          title={
            record.status === ACCOUNT_STATUS.ACTIVE
              ? 'Khóa tài khoản này?'
              : 'Mở khóa tài khoản này?'
          }
          onConfirm={() => handleToggleStatus(record)}
        >
          <Button
            danger={record.status === ACCOUNT_STATUS.ACTIVE}
            icon={record.status === ACCOUNT_STATUS.ACTIVE ? <LockOutlined /> : <UnlockOutlined />}
            size="small"
          >
            {record.status === ACCOUNT_STATUS.ACTIVE ? 'Khóa' : 'Mở khóa'}
          </Button>
        </Popconfirm>
      ),
    },
  ]

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }}>
        <Typography.Title level={3} style={{ margin: 0 }}>
          Quản lý tài khoản
        </Typography.Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setCreateOpen(true)}>
          Thêm tài khoản
        </Button>
      </Space>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={accounts}
        loading={loading}
        bordered
      />

      <Modal
        title="Thêm tài khoản mới"
        open={createOpen}
        onCancel={() => setCreateOpen(false)}
        onOk={() => form.submit()}
        confirmLoading={creating}
        okText="Tạo"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item
            name="fullName"
            label="Họ tên"
            rules={[{ required: true, message: 'Vui lòng nhập họ tên' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="username"
            label="Tài khoản"
            rules={[{ required: true, message: 'Vui lòng nhập tên tài khoản' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="password"
            label="Mật khẩu tạm thời"
            rules={[{ required: true, message: 'Vui lòng nhập mật khẩu' }]}
          >
            <Input.Password />
          </Form.Item>
          <Form.Item
            name="role"
            label="Vai trò"
            rules={[{ required: true, message: 'Vui lòng chọn vai trò' }]}
          >
            <Select
              placeholder="Chọn vai trò"
              options={Object.values(ROLES).map((r) => ({ value: r, label: ROLE_LABELS[r] }))}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
