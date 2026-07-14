import React from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Result } from 'antd'

function NotFound() {
  const navigate = useNavigate()

  return (
    <Result
      status="404"
      title="404"
      subTitle="Trang bạn tìm kiếm không tồn tại."
      extra={
        <Button type="primary" onClick={() => navigate('/')}>
          Quay về trang chủ
        </Button>
      }
    />
  )
}

export default NotFound
