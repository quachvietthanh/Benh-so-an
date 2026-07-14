import React from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { Menu } from 'antd'
import {
  DashboardOutlined,
  UserOutlined,
  FileTextOutlined,
} from '@ant-design/icons'

const menuItems = [
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

function Sidebar() {
  const navigate = useNavigate()
  const location = useLocation()

  const handleClick = (info) => {
    navigate(info.key)
  }

  return (
    <Menu
      theme="dark"
      mode="inline"
      selectedKeys={[location.pathname]}
      items={menuItems}
      onClick={handleClick}
    />
  )
}

export default Sidebar
