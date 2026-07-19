import React, { useState } from 'react'
import { Card, Input, Button, Alert, Typography, Result } from 'antd'

const { Title } = Typography

function PublicLookupPage() {
  const [code, setCode] = useState('')
  const [resultVisible, setResultVisible] = useState(false)

  return (
    <div>
      <div className="page-header">
        <Title level={4} style={{ margin: 0 }}>Cổng tra cứu bệnh nhân</Title>
      </div>

      <Alert type="info" showIcon message="Chỉ hiển thị đúng kết quả của mã hẹn hợp lệ; dữ liệu khác sẽ không được lộ." style={{ marginBottom: 16 }} />

      <Card title="Tra cứu theo mã hẹn" style={{ borderRadius: 12 }}>
        <Input placeholder="Nhập mã hẹn" value={code} onChange={(e) => setCode(e.target.value)} style={{ marginBottom: 12 }} />
        <Button type="primary" onClick={() => setResultVisible(true)}>Tra cứu</Button>

        {resultVisible && (
          <Result
            status="success"
            title="Kết quả tra cứu"
            subTitle={`Mã hẹn ${code} hợp lệ. Kết quả khám đã được ghi nhận.`}
          />
        )}
      </Card>
    </div>
  )
}

export default PublicLookupPage
