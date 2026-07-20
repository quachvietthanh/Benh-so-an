import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Form, Input, Button, message } from 'antd'
import { UserOutlined, LockOutlined, HospitalOutlined } from '@ant-design/icons'
import { useAuthContext } from '../context/AuthContext'

function Login() {
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { login, isAuthenticated } = useAuthContext()

  // Redirect if already logged in
  if (isAuthenticated) {
    navigate('/', { replace: true })
    return null
  }

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      const result = await login(values)
      if (result.success) {
        message.success('Đăng nhập thành công!')
        navigate('/', { replace: true })
      } else {
        message.error(result.message || 'Đăng nhập thất bại')
      }
    } catch (error) {
      message.error('Đã xảy ra lỗi hệ thống')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <div className="login-card">
        <div style={{ textAlign: 'center', marginBottom: 16 }}>
          <HospitalOutlined style={{ fontSize: 48, color: '#1890ff' }} />
        </div>
        <h1>Bệnh số án</h1>
        <p>Hệ thống chuyển đổi số cơ sở khám chữa bệnh</p>

        <Form
          name="login"
          onFinish={handleSubmit}
          layout="vertical"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: 'Vui lòng nhập tên đăng nhập' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="Tên đăng nhập"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: 'Vui lòng nhập mật khẩu' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Mật khẩu"
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              style={{ height: 44, fontSize: 16 }}
            >
              Đăng nhập
            </Button>
          </Form.Item>
        </Form>
      </div>
    </div>
  )
}

export default Login
