import React, { useState } from 'react'
import { Card, Table, Tag, Typography, Alert } from 'antd'
import { getServices } from '../services/mockDataService'

const { Title } = Typography

function ServicesPage() {
  const [services] = useState(getServices())

  return (
    <div>
      <div className="page-header">
        <Title level={4} style={{ margin: 0 }}>Danh mục dịch vụ & bảng giá</Title>
      </div>

      <Alert type="info" showIcon message="Giá dịch vụ hiệu lực theo thời điểm lập hóa đơn, không phụ thuộc giá hiện tại nếu bảng giá thay đổi." style={{ marginBottom: 16 }} />
      <Card title="Danh sách dịch vụ" style={{ borderRadius: 12 }}>
        <Table
          columns={[
            { title: 'Tên dịch vụ', dataIndex: 'name', key: 'name' },
            { title: 'Giá', dataIndex: 'price', key: 'price', render: (price) => `${price.toLocaleString('vi-VN')} ₫` },
            { title: 'Hiệu lực từ', dataIndex: 'effectiveFrom', key: 'effectiveFrom' },
            { title: 'Trạng thái', dataIndex: 'status', key: 'status', render: (status) => <Tag color="green">{status}</Tag> },
          ]}
          dataSource={services}
          rowKey="id"
          pagination={false}
        />
      </Card>
    </div>
  )
}

export default ServicesPage
