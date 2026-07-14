import { Typography } from 'antd'
import { useAuth } from '../../contexts/AuthContext'

export default function DashboardPage() {
  const { user } = useAuth()
  return (
    <div>
      <Typography.Title level={3}>Xin chào, {user?.fullName} 👋</Typography.Title>
      <Typography.Text type="secondary">
        Chọn chức năng ở menu bên trái để bắt đầu.
      </Typography.Text>
    </div>
  )
}
