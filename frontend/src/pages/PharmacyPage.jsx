import React, { useState } from 'react'
import {
  Card, Table, Tag, Button, Modal, Form, Select, InputNumber,
  Input, Space, Typography, Alert, Tabs, Divider, Badge, Popconfirm
} from 'antd'
import {
  PlusOutlined, InboxOutlined, MedicineBoxOutlined, HistoryOutlined, WarningOutlined
} from '@ant-design/icons'
import {
  getMedicines, getStockLogs, addMedicineToStock, issueMedicineByQty
} from '../services/mockDataService'

const { Title, Text } = Typography
const { TextArea } = Input

function PharmacyPage() {
  const [medicines, setMedicines] = useState(getMedicines())
  const [stockLogs, setStockLogs] = useState(getStockLogs())

  const [importVisible, setImportVisible] = useState(false)
  const [issueVisible, setIssueVisible] = useState(false)
  const [targetMedicine, setTargetMedicine] = useState(null)

  const [formImport] = Form.useForm()
  const [formIssue] = Form.useForm()

  const refresh = () => {
    setMedicines(getMedicines())
    setStockLogs(getStockLogs())
  }

  const handleImport = () => {
    formImport.validateFields().then((vals) => {
      addMedicineToStock(vals)
      refresh()
      setImportVisible(false)
      formImport.resetFields()
    })
  }

  const handleIssue = () => {
    formIssue.validateFields().then((vals) => {
      try {
        issueMedicineByQty(targetMedicine.id, vals.quantity, vals.note)
        refresh()
        setIssueVisible(false)
        formIssue.resetFields()
      } catch (err) {
        alert(err.message)
      }
    })
  }

  const openIssue = (medicine) => {
    if (medicine.stock <= 0) {
      alert('Không thể cấp phát: thuốc đã hết tồn kho!')
      return
    }
    const today = new Date().toISOString().slice(0, 10)
    if (medicine.expiryDate < today) {
      alert('Không thể cấp phát: thuốc đã hết hạn sử dụng!')
      return
    }
    setTargetMedicine(medicine)
    formIssue.setFieldsValue({ quantity: 1 })
    setIssueVisible(true)
  }

  const stockStatusTag = (rec) => {
    if (rec.stock <= 0) return <Badge status="error" text="Hết hàng" />
    if (rec.stock < rec.minStock) return <Badge status="warning" text="Sắp hết" />
    return <Badge status="success" text="Đủ hàng" />
  }

  const medicineColumns = [
    { title: 'Tên thuốc', dataIndex: 'name', key: 'name', render: (t) => <Text strong>{t}</Text> },
    { title: 'Nhóm', dataIndex: 'category', key: 'category', render: (t) => <Tag color="blue">{t}</Tag> },
    { title: 'Số lô', dataIndex: 'lot', key: 'lot' },
    {
      title: 'Tồn kho',
      dataIndex: 'stock',
      key: 'stock',
      render: (stock, rec) => (
        <Text strong style={{ color: stock < rec.minStock ? '#ef4444' : '#10b981' }}>{stock}</Text>
      ),
    },
    { title: 'Ngưỡng tối thiểu', dataIndex: 'minStock', key: 'minStock' },
    { title: 'Hạn dùng', dataIndex: 'expiryDate', key: 'expiryDate' },
    {
      title: 'Trạng thái',
      key: 'status',
      render: (_, rec) => stockStatusTag(rec),
    },
    {
      title: 'Thao tác',
      key: 'actions',
      render: (_, record) => (
        <Button
          type="primary"
          size="small"
          icon={<MedicineBoxOutlined />}
          onClick={() => openIssue(record)}
          disabled={record.stock <= 0}
        >
          Cấp phát
        </Button>
      ),
    },
  ]

  const logColumns = [
    {
      title: 'Loại',
      dataIndex: 'type',
      key: 'type',
      render: (t) => t === 'IMPORT'
        ? <Tag color="green">Nhập kho</Tag>
        : <Tag color="orange">Cấp phát</Tag>,
    },
    { title: 'Thuốc', dataIndex: 'medicineName', key: 'medicineName' },
    {
      title: 'Số lượng',
      dataIndex: 'quantity',
      key: 'quantity',
      render: (qty, rec) => (
        <Text strong style={{ color: rec.type === 'IMPORT' ? '#10b981' : '#f59e0b' }}>
          {rec.type === 'IMPORT' ? '+' : '-'}{qty}
        </Text>
      ),
    },
    { title: 'Số lô', dataIndex: 'lot', key: 'lot' },
    { title: 'Hạn dùng', dataIndex: 'expiryDate', key: 'expiryDate' },
    { title: 'Ghi chú', dataIndex: 'note', key: 'note', render: (n) => n || '—' },
    { title: 'Thực hiện bởi', dataIndex: 'createdBy', key: 'createdBy' },
    {
      title: 'Thời gian',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (d) => new Date(d).toLocaleString('vi-VN'),
    },
  ]

  const lowStockCount = medicines.filter((m) => m.stock < m.minStock && m.stock > 0).length
  const outOfStockCount = medicines.filter((m) => m.stock <= 0).length

  return (
    <div>
      <div className="page-header">
        <div>
          <Title className="page-title" level={3}>Quản lý kho &amp; Cấp phát thuốc</Title>
          <Text type="secondary">Danh mục thuốc, nhập kho, cấp phát và cảnh báo tồn kho (NCL-06)</Text>
        </div>
        <Button type="primary" icon={<InboxOutlined />} size="large" onClick={() => setImportVisible(true)}>
          Nhập kho mới
        </Button>
      </div>

      <div style={{ display: 'flex', gap: 16, marginBottom: 16 }}>
        {outOfStockCount > 0 && (
          <Alert type="error" showIcon icon={<WarningOutlined />}
            message={`${outOfStockCount} thuốc đã HẾT HÀNG — Cần nhập kho gấp!`}
            style={{ flex: 1 }}
          />
        )}
        {lowStockCount > 0 && (
          <Alert type="warning" showIcon
            message={`${lowStockCount} thuốc SẮP HẾT (dưới ngưỡng tối thiểu)`}
            style={{ flex: 1 }}
          />
        )}
      </div>

      <Tabs
        defaultActiveKey="inventory"
        items={[
          {
            key: 'inventory',
            label: <Space><MedicineBoxOutlined />Danh mục tồn kho</Space>,
            children: (
              <Card style={{ borderRadius: 12 }}>
                <Table
                  columns={medicineColumns}
                  dataSource={medicines}
                  rowKey="id"
                  pagination={{ pageSize: 8 }}
                  rowClassName={(rec) => rec.stock <= 0 ? 'row-danger' : rec.stock < rec.minStock ? 'row-warning' : ''}
                />
              </Card>
            ),
          },
          {
            key: 'logs',
            label: <Space><HistoryOutlined />Lịch sử xuất/nhập</Space>,
            children: (
              <Card style={{ borderRadius: 12 }}>
                <Table
                  columns={logColumns}
                  dataSource={stockLogs}
                  rowKey="id"
                  pagination={{ pageSize: 10 }}
                />
              </Card>
            ),
          },
        ]}
      />

      {/* Modal Nhập kho */}
      <Modal
        title={<Space><InboxOutlined style={{ color: '#10b981' }} /><span>Nhập kho thuốc mới</span></Space>}
        open={importVisible}
        onCancel={() => { setImportVisible(false); formImport.resetFields() }}
        onOk={handleImport}
        okText="Xác nhận nhập kho"
        cancelText="Hủy"
        destroyOnClose
        width={520}
      >
        <Form form={formImport} layout="vertical">
          <Form.Item label="Thuốc" name="medicineId" rules={[{ required: true, message: 'Chọn thuốc' }]}>
            <Select placeholder="Chọn thuốc cần nhập" showSearch optionFilterProp="children">
              {medicines.map((m) => (
                <Select.Option key={m.id} value={m.id}>
                  {m.name} (tồn: {m.stock})
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item label="Số lượng nhập" name="quantity" rules={[{ required: true, message: 'Nhập số lượng' }]}>
            <InputNumber min={1} style={{ width: '100%' }} placeholder="Nhập số lượng" />
          </Form.Item>
          <Form.Item label="Số lô mới (nếu có)" name="lot">
            <Input placeholder="VD: LOT-AM-009" />
          </Form.Item>
          <Form.Item label="Hạn sử dụng mới (nếu có)" name="expiryDate">
            <Input type="date" />
          </Form.Item>
          <Form.Item label="Ghi chú" name="note">
            <TextArea rows={2} placeholder="Ghi chú nhập kho..." />
          </Form.Item>
        </Form>
      </Modal>

      {/* Modal Cấp phát */}
      <Modal
        title={<Space><MedicineBoxOutlined style={{ color: '#f59e0b' }} /><span>Cấp phát thuốc: {targetMedicine?.name}</span></Space>}
        open={issueVisible}
        onCancel={() => { setIssueVisible(false); formIssue.resetFields() }}
        onOk={handleIssue}
        okText="Xác nhận cấp phát"
        cancelText="Hủy"
        destroyOnClose
        width={460}
      >
        {targetMedicine && (
          <Alert
            type="info"
            showIcon
            message={`Tồn kho hiện tại: ${targetMedicine.stock} | Số lô: ${targetMedicine.lot} | Hạn: ${targetMedicine.expiryDate}`}
            style={{ marginBottom: 16 }}
          />
        )}
        <Form form={formIssue} layout="vertical">
          <Form.Item label="Số lượng cấp phát" name="quantity" rules={[{ required: true, message: 'Nhập số lượng' }]}>
            <InputNumber
              min={1}
              max={targetMedicine?.stock}
              style={{ width: '100%' }}
              placeholder="Nhập số lượng cấp phát"
            />
          </Form.Item>
          <Form.Item label="Ghi chú (VD: cấp theo đơn BA-XXXX)" name="note">
            <TextArea rows={2} placeholder="Ghi chú cấp phát..." />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default PharmacyPage
