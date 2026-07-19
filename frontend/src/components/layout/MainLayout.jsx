import React, { useState } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { Layout, Menu, Dropdown, Avatar, Space, Typography, Alert } from 'antd'
import {
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  MedicineBoxOutlined,
  UserOutlined,
} from '@ant-design/icons'
import { useAuthContext } from '../../context/AuthContext'
import { getNavigationItems } from '../../services/mockDataService'

const { Header, Sider, Content } = Layout
const { Text } = Typography

function MainLayout() {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const { user, logout } = useAuthContext()

  const handleMenuClick = (info) => {
    navigate(info.key)
  }

  const sidebarItems = getNavigationItems(user?.roles || []).map((item) => ({
    key: item.key,
    icon: React.createElement(item.icon),
    label: item.label,
  }))

  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: 'Thông tin cá nhân',
    },
    { type: 'divider' },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Đăng xuất',
      danger: true,
      onClick: () => {
        logout()
        navigate('/login')
      },
    },
  ]

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        theme="light"
        width={260}
        style={{ borderRight: '1px solid var(--border-color)' }}
      >
        <div className="logo" onClick={() => navigate('/')} style={{ cursor: 'pointer' }}>
          <MedicineBoxOutlined style={{ fontSize: 24, marginRight: collapsed ? 0 : 8 }} />
          {!collapsed && <span style={{ whiteSpace: 'nowrap' }}>Bệnh Án Số</span>}
        </div>

        <Menu
          theme="light"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={sidebarItems}
          onClick={handleMenuClick}
          style={{ borderRight: 0, padding: '0 8px' }}
        />
      </Sider>

      <Layout>
        <Header>
          <Space>
            {React.createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
              style: {
                fontSize: '18px',
                cursor: 'pointer',
                padding: '8px',
                borderRadius: '8px',
                backgroundColor: 'var(--bg-color)'
              },
              onClick: () => setCollapsed(!collapsed),
            })}
          </Space>

          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight" arrow>
            <Space style={{ cursor: 'pointer', padding: '4px 12px', borderRadius: '24px', backgroundColor: 'var(--bg-color)', border: '1px solid var(--border-color)' }}>
              <Avatar icon={<UserOutlined />} style={{ backgroundColor: 'var(--primary-color)' }} />
              <div style={{ display: 'flex', flexDirection: 'column', lineHeight: '1.2' }}>
                <Text strong style={{ fontSize: '14px' }}>{user?.fullName || user?.username}</Text>
                <Text type="secondary" style={{ fontSize: '12px', textTransform: 'capitalize' }}>
                  {user?.roles?.[0] || 'User'}
                </Text>
              </div>
            </Space>
          </Dropdown>
        </Header>

        <Content style={{ margin: '24px', minHeight: 280, display: 'flex', flexDirection: 'column' }}>
          <Alert
            message="Chế độ Demo"
            description="Hệ thống đang sử dụng dữ liệu mô phỏng. Phù hợp cho việc trình diễn nghiệp vụ phòng khám nhỏ."
            type="info"
            showIcon
            closable
            style={{ marginBottom: 24, borderRadius: '12px', border: 'none', backgroundColor: '#e0f2fe', color: '#0284c7' }}
          />
          <div className="animate-fade-in" style={{ flex: 1 }}>
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  )
}

export default MainLayout
