import React, { useState } from 'react'
import { Card, Table, Tag, Button, Alert, Space, Typography } from 'antd'
import { getInvoices } from '../services/mockDataService'

const { Title } = Typography

function BillingPage() {
  const [invoices] = useState(getInvoices())

  const columns = [
    { title: 'Mã hóa đơn', dataIndex: 'invoiceCode', key: 'invoiceCode' },
    { title: 'Bệnh nhân', dataIndex: 'patientName', key: 'patientName' },
    { title: 'Số tiền', dataIndex: 'amount', key: 'amount', render: (amount) => `${amount.toLocaleString('vi-VN')} ₫` },
    { title: 'Trạng thái', dataIndex: 'status', key: 'status', render: (status) => <Tag color={status === 'PAID' ? 'green' : 'orange'}>{status}</Tag> },
    { title: 'Thao tác', key: 'actions', render: () => <Button>Điều chỉnh</Button> },
  ]

  return (
    <div>
      <div className="page-header">
        <Title level={4} style={{ margin: 0 }}>Thu phí & hóa đơn</Title>
        <Button type="primary">Tạo hóa đơn mới</Button>
      </div>

      <Alert type="info" showIcon message="Hóa đơn gốc là bất biến; giao diện chỉ tạo luồng điều chỉnh hóa đơn với lý do rõ ràng." style={{ marginBottom: 16 }} />

      <Card title="Danh sách hóa đơn" style={{ borderRadius: 12 }}>
        <Table columns={columns} dataSource={invoices} rowKey="id" pagination={false} />
      </Card>
    </div>
  )
}

export default BillingPage
