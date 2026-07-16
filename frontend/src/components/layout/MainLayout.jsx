import React, { useState } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { Layout, Menu, Dropdown, Avatar, Space, Typography } from 'antd'
import {
  DashboardOutlined,
  UserOutlined,
  FileTextOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  HospitalOutlined,
  CalendarOutlined,
  MedicineBoxOutlined,
  TeamOutlined,
  SettingOutlined,
  SafetyCertificateOutlined,
  FileProtectOutlined,
} from '@ant-design/icons'
import { useAuthContext } from '../../context/AuthContext'
import RoleProtected from '../common/RoleProtected'

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

  // Role-based sidebar items
  const sidebarItems = [
    {
      key: '/',
      icon: <DashboardOutlined />,
      label: 'Tổng quan',
    },
    {
      key: '/patients',
      icon: <TeamOutlined />,
      label: 'Bệnh nhân',
      roles: ['ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST'],
    },
    {
      key: '/medical-records',
      icon: <FileTextOutlined />,
      label: 'Hồ sơ bệnh án',
      roles: ['ADMIN', 'DOCTOR', 'NURSE'],
    },
    {
      key: '/prescriptions',
      icon: <MedicineBoxOutlined />,
      label: 'Đơn thuốc',
      roles: ['ADMIN', 'DOCTOR', 'PHARMACIST'],
    },
    {
      key: '/appointments',
      icon: <CalendarOutlined />,
      label: 'Lịch hẹn',
      roles: ['ADMIN', 'DOCTOR', 'RECEPTIONIST'],
    },
    {
      key: '/pharmacy',
      icon: <SafetyCertificateOutlined />,
      label: 'Nhà thuốc',
      roles: ['ADMIN', 'PHARMACIST'],
    },
    {
      key: '/invoices',
      icon: <FileProtectOutlined />,
      label: 'Hóa đơn',
      roles: ['ADMIN', 'RECEPTIONIST'],
    },
  ]

  // Filter sidebar items based on user roles
  const userRoles = user?.roles || []
  const filteredItems = sidebarItems
    .filter(item => {
      if (!item.roles) return true // always show if no role restriction
      return item.roles.some(role => userRoles.includes(role))
    })
    .map(({ roles, ...item }) => item) // remove roles prop from menu items

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        theme="dark"
        width={240}
      >
        <div className="logo" style={{ cursor: 'pointer' }} onClick={() => navigate('/')}>
          <HospitalOutlined style={{ fontSize: 24, marginRight: collapsed ? 0 : 8 }} />
          {!collapsed && <span>Bệnh số án</span>}
        </div>

        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={filteredItems}
          onClick={handleMenuClick}
        />
      </Sider>

      <Layout>
        <Header>
          <Space>
            {React.createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
              style: { fontSize: '18px', cursor: 'pointer' },
              onClick: () => setCollapsed(!collapsed),
            })}
          </Space>

          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <Space style={{ cursor: 'pointer' }}>
              <Avatar icon={<UserOutlined />} style={{ backgroundColor: '#1890ff' }} />
              <Text strong>{user?.fullName || user?.username}</Text>
              <Text type="secondary" style={{ fontSize: 12 }}>
                ({user?.roles?.join(', ')})
              </Text>
            </Space>
          </Dropdown>
        </Header>

        <Content style={{ margin: 24, minHeight: 280 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}

export default MainLayout
