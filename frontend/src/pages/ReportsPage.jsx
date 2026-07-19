import React, { useState } from 'react'
import { Card, Row, Col, Table, Tag, Typography, Alert } from 'antd'
import { getAuditLogs, getInvoices, getMedicines } from '../services/mockDataService'

const { Title } = Typography

function ReportsPage() {
  const [auditLogs] = useState(getAuditLogs())
  const [invoices] = useState(getInvoices())
  const [medicines] = useState(getMedicines())

  return (
    <div>
      <div className="page-header">
        <Title level={4} style={{ margin: 0 }}>Báo cáo vận hành</Title>
      </div>

      <Alert type="success" showIcon message="Báo cáo vận hành và nhật ký truy cập đều có thể tra cứu theo thời gian, người dùng và bệnh nhân." style={{ marginBottom: 16 }} />

      <Row gutter={16}>
        <Col span={8}>
          <Card title="Doanh thu theo kỳ" style={{ borderRadius: 12 }}>
            <p style={{ fontSize: 28, fontWeight: 700 }}>2.850.000 ₫</p>
          </Card>
        </Col>
        <Col span={8}>
          <Card title="Thuốc dùng nhiều" style={{ borderRadius: 12 }}>
            <p>{medicines[0]?.name}</p>
          </Card>
        </Col>
        <Col span={8}>
          <Card title="Lượt khám hôm nay" style={{ borderRadius: 12 }}>
            <p style={{ fontSize: 28, fontWeight: 700 }}>18</p>
          </Card>
        </Col>
      </Row>

      <Card title="Nhật ký truy cập bệnh án" style={{ borderRadius: 12, marginTop: 16 }}>
        <Table
          columns={[
            { title: 'Người dùng', dataIndex: 'user', key: 'user' },
            { title: 'Bệnh nhân', dataIndex: 'patient', key: 'patient' },
            { title: 'Hành động', dataIndex: 'action', key: 'action' },
            { title: 'Thời gian', dataIndex: 'time', key: 'time' },
          ]}
          dataSource={auditLogs}
          rowKey="id"
          pagination={false}
        />
      </Card>

      <Card title="Hóa đơn và điều chỉnh" style={{ borderRadius: 12, marginTop: 16 }}>
        <Table
          columns={[
            { title: 'Mã hóa đơn', dataIndex: 'invoiceCode', key: 'invoiceCode' },
            { title: 'Bệnh nhân', dataIndex: 'patientName', key: 'patientName' },
            { title: 'Số tiền', dataIndex: 'amount', key: 'amount', render: (amount) => `${amount.toLocaleString('vi-VN')} ₫` },
            { title: 'Trạng thái', dataIndex: 'status', key: 'status', render: (status) => <Tag color={status === 'PAID' ? 'green' : 'orange'}>{status}</Tag> },
          ]}
          dataSource={invoices}
          rowKey="id"
          pagination={false}
        />
      </Card>
    </div>
  )
}

export default ReportsPage
