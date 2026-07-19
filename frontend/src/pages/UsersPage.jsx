import React, { useState } from 'react'
import { Card, Table, Tag, Button, Typography, Alert } from 'antd'
import { getUsers } from '../services/mockDataService'

const { Title } = Typography

function UsersPage() {
  const [users] = useState(getUsers())

  const columns = [
    { title: 'Tên đăng nhập', dataIndex: 'username', key: 'username' },
    { title: 'Họ tên', dataIndex: 'fullName', key: 'fullName' },
    { title: 'Vai trò', dataIndex: 'roles', key: 'roles', render: (roles) => roles.map((role) => <Tag key={role}>{role}</Tag>) },
    { title: 'Thao tác', key: 'actions', render: () => <Button>Quản lý</Button> },
  ]

  return (
    <div>
      <div className="page-header">
        <Title level={4} style={{ margin: 0 }}>Quản trị tài khoản</Title>
        <Button type="primary">Tạo tài khoản</Button>
      </div>

      <Alert type="warning" showIcon message="Quản trị viên có thể tạo, khóa/mở và gán vai trò cho tài khoản người dùng." style={{ marginBottom: 16 }} />
      <Card title="Danh sách người dùng" style={{ borderRadius: 12 }}>
        <Table columns={columns} dataSource={users} rowKey="id" pagination={false} />
      </Card>
    </div>
  )
}

export default UsersPage
