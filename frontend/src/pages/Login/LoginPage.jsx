import { useState } from 'react'
import { Form, Input, Button, Card, Typography, Alert } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate, useLocation, useSearchParams } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'
import { ROLE_HOME_ROUTE } from '../../utils/constants'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [searchParams] = useSearchParams()
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  // NCL-01-CN-001-TC-03: phiên hết hạn -> quay lại trang login kèm cảnh báo
  const sessionExpired = searchParams.get('expired') === '1'

  const onFinish = async (values) => {
    setError(null)
    setSubmitting(true)
    try {
      const user = await login(values.username, values.password)
      const redirectTo = location.state?.from?.pathname || ROLE_HOME_ROUTE[user.role] || '/'
      navigate(redirectTo, { replace: true })
    } catch (err) {
      // NCL-01-CN-001-TC-02: sai tài khoản/mật khẩu -> báo lỗi, không cho vào
      const message =
        err.response?.status === 401
          ? 'Tài khoản hoặc mật khẩu không đúng.'
          : 'Đăng nhập thất bại. Vui lòng thử lại.'
      setError(message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #0f766e 0%, #134e4a 100%)',
      }}
    >
      <Card style={{ width: 380 }} bordered={false}>
        <Typography.Title level={3} style={{ textAlign: 'center', marginBottom: 4 }}>
          🏥 Bệnh Số Án
        </Typography.Title>
        <Typography.Text
          type="secondary"
          style={{ display: 'block', textAlign: 'center', marginBottom: 24 }}
        >
          Hệ thống quản lý hồ sơ khám chữa bệnh
        </Typography.Text>

        {sessionExpired && (
          <Alert
            type="warning"
            message="Phiên làm việc đã hết hạn, vui lòng đăng nhập lại."
            style={{ marginBottom: 16 }}
            showIcon
          />
        )}
        {error && (
          <Alert type="error" message={error} style={{ marginBottom: 16 }} showIcon />
        )}

        <Form layout="vertical" onFinish={onFinish} disabled={submitting}>
          <Form.Item
            name="username"
            label="Tài khoản"
            rules={[{ required: true, message: 'Vui lòng nhập tài khoản' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="Nhập tài khoản" autoFocus />
          </Form.Item>

          <Form.Item
            name="password"
            label="Mật khẩu"
            rules={[{ required: true, message: 'Vui lòng nhập mật khẩu' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Nhập mật khẩu" />
          </Form.Item>

          <Form.Item style={{ marginTop: 24 }}>
            <Button type="primary" htmlType="submit" block loading={submitting}>
              Đăng nhập
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}
