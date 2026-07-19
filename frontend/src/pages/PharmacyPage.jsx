import React, { useState } from 'react'
import { Card, Table, Button, InputNumber, Tag, Alert, Space } from 'antd'
import { WarningOutlined } from '@ant-design/icons'
import { getMedicines, updateMedicineStock } from '../services/mockDataService'

function PharmacyPage() {
  const [medicines, setMedicines] = useState(getMedicines())

  const handleIssue = (id) => {
    const medicine = medicines.find((item) => item.id === id)
    if (medicine?.stock <= 0) {
      alert('Không thể cấp phát do đã hết tồn kho')
      return
    }
    updateMedicineStock(id, -1)
    setMedicines(getMedicines())
  }

  const columns = [
    { title: 'Thuốc', dataIndex: 'name', key: 'name' },
    { title: 'Lô', dataIndex: 'lot', key: 'lot' },
    { title: 'Tồn kho', dataIndex: 'stock', key: 'stock' },
    { title: 'Ngưỡng', dataIndex: 'minStock', key: 'minStock' },
    { title: 'Hạn dùng', dataIndex: 'expiryDate', key: 'expiryDate' },
    {
      title: 'Trạng thái',
      key: 'status',
      render: (_, record) => {
        if (record.stock <= 0) return <Tag color="red">Hết hàng</Tag>
        if (record.stock < record.minStock) return <Tag color="orange">Sắp hết</Tag>
        return <Tag color="green">Đủ</Tag>
      },
    },
    {
      title: 'Thao tác',
      key: 'actions',
      render: (_, record) => <Button onClick={() => handleIssue(record.id)}>Cấp phát</Button>,
    },
  ]

  return (
    <div>
      <div className="page-header">
        <h2 style={{ margin: 0 }}>Quản lý kho thuốc</h2>
        <Button type="primary">Nhập kho mới</Button>
      </div>

      <Alert type="warning" showIcon message="Cấp phát bị chặn khi tồn kho không đủ hoặc thuốc đã hết hạn sử dụng." style={{ marginBottom: 16 }} />
      <Card title="Danh mục thuốc trong kho" style={{ borderRadius: 12 }}>
        <Table columns={columns} dataSource={medicines} rowKey="id" pagination={false} />
      </Card>
    </div>
  )
}

export default PharmacyPage
