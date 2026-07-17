import React, { useState } from 'react'
import {
  Card, Table, Tag, Button, Alert, Modal, Form, Select, InputNumber,
  Input, Space, Typography, Popconfirm, Divider, Badge
} from 'antd'
import {
  PlusOutlined, EditOutlined, DollarCircleOutlined, CheckCircleOutlined,
  FileTextOutlined, WarningOutlined
} from '@ant-design/icons'
import {
  getInvoices, createInvoice, adjustInvoice, markInvoicePaid, getPatients, getServices
} from '../services/mockDataService'

const { Title, Text } = Typography
const { TextArea } = Input

function BillingPage() {
  const [invoices, setInvoices] = useState(getInvoices())
  const [patients] = useState(getPatients())
  const [services] = useState(getServices())

  const [createVisible, setCreateVisible] = useState(false)
  const [adjustVisible, setAdjustVisible] = useState(false)
  const [selectedInvoice, setSelectedInvoice] = useState(null)
  const [formCreate] = Form.useForm()
  const [formAdjust] = Form.useForm()

  const [selectedItems, setSelectedItems] = useState([])
  const [adjustItems, setAdjustItems] = useState([])

  const refresh = () => setInvoices(getInvoices())

  // ── helpers ───────────────────────────────────────────────────────────────
  const calcTotal = (items) => items.reduce((s, it) => s + (it.price || 0) * (it.qty || 1), 0)

  const addServiceItem = (form, items, setItems) => {
    setItems([...items, { key: Date.now(), serviceId: '', serviceName: '', price: 0, qty: 1 }])
  }

  const updateItem = (items, setItems, key, field, value) => {
    setItems(items.map((it) => {
      if (it.key !== key) return it
      if (field === 'serviceId') {
        const svc = services.find((s) => s.id === value)
        return { ...it, serviceId: value, serviceName: svc?.name || '', price: svc?.price || 0 }
      }
      return { ...it, [field]: value }
    }))
  }

  const removeItem = (items, setItems, key) => setItems(items.filter((it) => it.key !== key))

  // ── handlers ──────────────────────────────────────────────────────────────
  const handleCreate = () => {
    formCreate.validateFields().then((vals) => {
      if (selectedItems.length === 0) { alert('Vui lòng thêm ít nhất một dịch vụ!'); return }
      const patient = patients.find((p) => p.id === vals.patientId)
      createInvoice({
        patientId: vals.patientId,
        patientName: patient?.fullName || '',
        items: selectedItems.map(({ serviceId, serviceName, price, qty }) => ({ serviceId, serviceName, price, qty })),
      })
      refresh()
      setCreateVisible(false)
      setSelectedItems([])
      formCreate.resetFields()
    })
  }

  const handleAdjust = () => {
    formAdjust.validateFields().then((vals) => {
      if (adjustItems.length === 0) { alert('Vui lòng thêm ít nhất một dịch vụ!'); return }
      adjustInvoice(selectedInvoice.id, {
        items: adjustItems.map(({ serviceId, serviceName, price, qty }) => ({ serviceId, serviceName, price, qty })),
        adjustmentReason: vals.adjustmentReason,
      })
      refresh()
      setAdjustVisible(false)
      setAdjustItems([])
      formAdjust.resetFields()
    })
  }

  const handleMarkPaid = (id) => {
    markInvoicePaid(id)
    refresh()
  }

  const openAdjust = (invoice) => {
    setSelectedInvoice(invoice)
    setAdjustItems(invoice.items?.map((it) => ({ ...it, key: it.serviceId + Date.now() })) || [])
    setAdjustVisible(true)
  }

  // ── item editor ───────────────────────────────────────────────────────────
  const ItemEditor = ({ items, setItems }) => (
    <div style={{ marginTop: 12 }}>
      {items.map((it) => (
        <div key={it.key} style={{ display: 'flex', gap: 8, marginBottom: 8, alignItems: 'center' }}>
          <Select
            style={{ flex: 2 }}
            placeholder="Chọn dịch vụ"
            value={it.serviceId || undefined}
            onChange={(val) => updateItem(items, setItems, it.key, 'serviceId', val)}
          >
            {services.filter((s) => s.status === 'ACTIVE').map((s) => (
              <Select.Option key={s.id} value={s.id}>
                {s.name} — {s.price.toLocaleString('vi-VN')} ₫
              </Select.Option>
            ))}
          </Select>
          <InputNumber
            style={{ width: 80 }}
            min={1}
            value={it.qty}
            onChange={(val) => updateItem(items, setItems, it.key, 'qty', val)}
            addonBefore="SL"
          />
          <Text style={{ width: 120, textAlign: 'right', fontWeight: 600 }}>
            {((it.price || 0) * (it.qty || 1)).toLocaleString('vi-VN')} ₫
          </Text>
          <Button danger size="small" onClick={() => removeItem(items, setItems, it.key)}>✕</Button>
        </div>
      ))}
      <Button icon={<PlusOutlined />} type="dashed" onClick={() => addServiceItem(null, items, setItems)} block>
        Thêm dịch vụ
      </Button>
      {items.length > 0 && (
        <div style={{ textAlign: 'right', marginTop: 8 }}>
          <Text strong style={{ fontSize: 16, color: '#0ea5e9' }}>
            Tổng: {calcTotal(items).toLocaleString('vi-VN')} ₫
          </Text>
        </div>
      )}
    </div>
  )

  // ── columns ───────────────────────────────────────────────────────────────
  const columns = [
    {
      title: 'Mã hóa đơn',
      dataIndex: 'invoiceCode',
      key: 'invoiceCode',
      render: (code, rec) => (
        <Space>
          <Text strong>{code}</Text>
          {rec.adjustmentOf && <Tag color="purple" style={{ fontSize: 10 }}>Điều chỉnh</Tag>}
        </Space>
      ),
    },
    { title: 'Bệnh nhân', dataIndex: 'patientName', key: 'patientName' },
    {
      title: 'Số tiền',
      dataIndex: 'amount',
      key: 'amount',
      render: (amount) => <Text strong style={{ color: '#10b981' }}>{amount.toLocaleString('vi-VN')} ₫</Text>,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Badge
          status={status === 'PAID' ? 'success' : 'warning'}
          text={status === 'PAID' ? 'Đã thanh toán' : 'Chờ thanh toán'}
        />
      ),
    },
    {
      title: 'Lý do điều chỉnh',
      dataIndex: 'adjustmentReason',
      key: 'adjustmentReason',
      render: (reason) => reason ? <Text type="secondary" style={{ fontSize: 12 }}>{reason}</Text> : '—',
    },
    {
      title: 'Ngày lập',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (d) => new Date(d).toLocaleString('vi-VN'),
    },
    {
      title: 'Thao tác',
      key: 'actions',
      render: (_, record) => (
        <Space>
          {record.status === 'PENDING' && !record.adjustmentOf && (
            <>
              <Popconfirm
                title="Xác nhận đã thanh toán?"
                onConfirm={() => handleMarkPaid(record.id)}
                okText="Có" cancelText="Hủy"
              >
                <Button size="small" type="primary" icon={<CheckCircleOutlined />}>
                  Thanh toán
                </Button>
              </Popconfirm>
              <Button size="small" icon={<EditOutlined />} onClick={() => openAdjust(record)}>
                Điều chỉnh
              </Button>
            </>
          )}
          {record.adjustmentOf && (
            <Tag color="purple">Đã điều chỉnh</Tag>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div>
      <div className="page-header">
        <div>
          <Title className="page-title" level={3}>Thu phí &amp; Hóa đơn</Title>
          <Text type="secondary">Lập hóa đơn, ghi nhận thanh toán và điều chỉnh có vết (NCL-07)</Text>
        </div>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={() => setCreateVisible(true)}>
          Tạo hóa đơn mới
        </Button>
      </div>

      <Alert
        type="info"
        showIcon
        message="Hóa đơn gốc là bất biến. Điều chỉnh sẽ tạo bản mới có ghi rõ lý do và liên kết đến hóa đơn gốc."
        style={{ marginBottom: 16 }}
      />

      <Card
        title={<Space><FileTextOutlined style={{ color: '#0ea5e9' }} /><span>Danh sách hóa đơn</span></Space>}
        style={{ borderRadius: 12 }}
      >
        <Table
          columns={columns}
          dataSource={invoices}
          rowKey="id"
          pagination={{ pageSize: 8 }}
          rowClassName={(rec) => rec.adjustmentOf ? 'adjustment-row' : ''}
        />
      </Card>

      {/* Modal tạo hóa đơn mới */}
      <Modal
        title={<Space><DollarCircleOutlined style={{ color: '#0ea5e9' }} /><span>Tạo hóa đơn mới</span></Space>}
        open={createVisible}
        onCancel={() => { setCreateVisible(false); setSelectedItems([]); formCreate.resetFields() }}
        onOk={handleCreate}
        okText="Xác nhận tạo"
        cancelText="Hủy"
        width={640}
        destroyOnClose
      >
        <Form form={formCreate} layout="vertical">
          <Form.Item label="Bệnh nhân" name="patientId" rules={[{ required: true, message: 'Vui lòng chọn bệnh nhân' }]}>
            <Select placeholder="Chọn bệnh nhân" showSearch optionFilterProp="children">
              {patients.map((p) => (
                <Select.Option key={p.id} value={p.id}>{p.fullName} — {p.patientCode}</Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Divider>Danh sách dịch vụ</Divider>
          <ItemEditor items={selectedItems} setItems={setSelectedItems} />
        </Form>
      </Modal>

      {/* Modal điều chỉnh hóa đơn */}
      <Modal
        title={<Space><WarningOutlined style={{ color: '#f59e0b' }} /><span>Điều chỉnh hóa đơn {selectedInvoice?.invoiceCode}</span></Space>}
        open={adjustVisible}
        onCancel={() => { setAdjustVisible(false); setAdjustItems([]) }}
        onOk={handleAdjust}
        okText="Tạo bản điều chỉnh"
        cancelText="Hủy"
        width={680}
        destroyOnClose
      >
        <Alert
          type="warning"
          showIcon
          message="Thao tác này sẽ tạo một hóa đơn điều chỉnh MỚI, hóa đơn gốc vẫn được lưu nguyên vẹn."
          style={{ marginBottom: 16 }}
        />
        <Form form={formAdjust} layout="vertical">
          <Form.Item
            label="Lý do điều chỉnh"
            name="adjustmentReason"
            rules={[{ required: true, message: 'Vui lòng ghi rõ lý do điều chỉnh' }]}
          >
            <TextArea rows={3} placeholder="Nhập lý do điều chỉnh hóa đơn (bắt buộc)..." />
          </Form.Item>
          <Divider>Dịch vụ mới sau điều chỉnh</Divider>
          <ItemEditor items={adjustItems} setItems={setAdjustItems} />
        </Form>
      </Modal>
    </div>
  )
}

export default BillingPage
