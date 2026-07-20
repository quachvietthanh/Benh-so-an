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
} from '@ant-design/icons'
import { useAuthContext } from '../../context/AuthContext'

const { Header, Sider, Content } = Layout
const { Text } = Typography

const sidebarItems = [
  {
    key: '/',
    icon: <DashboardOutlined />,
    label: 'Tổng quan',
  },
  {
    key: '/patients',
    icon: <UserOutlined />,
    label: 'Bệnh nhân',
  },
  {
    key: '/medical-records',
    icon: <FileTextOutlined />,
    label: 'Hồ sơ bệnh án',
  },
]

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
          items={sidebarItems}
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
