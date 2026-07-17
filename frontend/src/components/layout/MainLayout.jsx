import { useMemo } from 'react'
import { Layout, Menu, Avatar, Dropdown, Typography } from 'antd'
import {
  UserOutlined,
  TeamOutlined,
  CalendarOutlined,
  FileTextOutlined,
  MedicineBoxOutlined,
  DollarOutlined,
  LogoutOutlined,
  SettingOutlined,
} from '@ant-design/icons'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'
import { ROLES, ROLE_LABELS } from '../../utils/constants'

const { Header, Sider, Content } = Layout

const MENU_BY_ROLE = {
  [ROLES.ADMIN]: [
    {
      key: '/admin/accounts',
      icon: <SettingOutlined />,
      label: 'Quản lý tài khoản',
    },
  ],
  [ROLES.RECEPTIONIST]: [
    {
      key: '/patients',
      icon: <TeamOutlined />,
      label: 'Hồ sơ bệnh nhân',
    },
    {
      key: '/appointments',
      icon: <CalendarOutlined />,
      label: 'Lịch hẹn',
    },
  ],
  [ROLES.DOCTOR]: [
    {
      key: '/appointments/queue',
      icon: <CalendarOutlined />,
      label: 'Hàng đợi khám',
    },
    {
      key: '/records',
      icon: <FileTextOutlined />,
      label: 'Bệnh án',
    },
  ],
  [ROLES.PHARMACIST]: [
    {
      key: '/pharmacy/dispense',
      icon: <MedicineBoxOutlined />,
      label: 'Cấp phát thuốc',
    },
    {
      key: '/pharmacy/inventory',
      icon: <MedicineBoxOutlined />,
      label: 'Kho thuốc',
    },
  ],
  [ROLES.CASHIER]: [
    {
      key: '/payments',
      icon: <DollarOutlined />,
      label: 'Thanh toán',
    },
  ],
}

export default function MainLayout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const menuItems = useMemo(
    () => MENU_BY_ROLE[user?.role] || [],
    [user]
  )

  const userMenuItems = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Đăng xuất',
    },
  ]

  const handleUserMenuClick = ({ key }) => {
    if (key === 'logout') {
      logout().then(() => navigate('/login'))
    }
  }

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider breakpoint="lg" collapsedWidth="0">
        <div
          style={{
            color: '#fff',
            padding: 16,
            fontWeight: 600,
            fontSize: 16,
          }}
        >
          🏥 Bệnh Án Số
        </div>

        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>

      <Layout>
        <Header
          style={{
            background: '#fff',
            display: 'flex',
            justifyContent: 'flex-end',
            alignItems: 'center',
            padding: '0 24px',
          }}
        >
          <Dropdown
            menu={{
              items: userMenuItems,
              onClick: handleUserMenuClick,
            }}
          >
            <div
              style={{
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: 8,
              }}
            >
              <Avatar icon={<UserOutlined />} />

              <div>
                <Typography.Text strong>
                  {user?.fullName}
                </Typography.Text>

                <br />

                <Typography.Text
                  type="secondary"
                  style={{ fontSize: 12 }}
                >
                  {ROLE_LABELS[user?.role]}
                </Typography.Text>
              </div>
            </div>
          </Dropdown>
        </Header>

        <Content style={{ margin: 24 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}