import React, { useState } from 'react'
import {
  Card, Table, Tag, Typography, Alert, Button, Modal, Form,
  Input, InputNumber, Select, Space, Popconfirm, Badge, Tooltip
} from 'antd'
import {
  PlusOutlined, EditOutlined, DeleteOutlined, SettingOutlined, CheckCircleOutlined, StopOutlined
} from '@ant-design/icons'
import { getServices, createService, updateService, deleteService } from '../services/mockDataService'

const { Title, Text } = Typography
const { TextArea } = Input

const CATEGORIES = ['Khám bệnh', 'Xét nghiệm', 'Chẩn đoán hình ảnh', 'Thủ thuật', 'Phục hồi chức năng', 'Khác']

function ServicesPage() {
  const [services, setServices] = useState(getServices())
  const [visible, setVisible] = useState(false)
  const [editTarget, setEditTarget] = useState(null)
  const [form] = Form.useForm()

  const refresh = () => setServices(getServices())

  const openCreate = () => {
    setEditTarget(null)
    form.resetFields()
    form.setFieldsValue({ status: 'ACTIVE', effectiveFrom: new Date().toISOString().slice(0, 10) })
    setVisible(true)
  }

  const openEdit = (service) => {
    setEditTarget(service)
    form.setFieldsValue(service)
    setVisible(true)
  }

  const handleSave = () => {
    form.validateFields().then((vals) => {
      if (editTarget) {
        updateService(editTarget.id, vals)
      } else {
        createService(vals)
      }
      refresh()
      setVisible(false)
      form.resetFields()
    })
  }

  const handleDelete = (id) => {
    deleteService(id)
    refresh()
  }

  const handleToggleStatus = (service) => {
    updateService(service.id, { ...service, status: service.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' })
    refresh()
  }

  const columns = [
    {
      title: 'Tên dịch vụ',
      dataIndex: 'name',
      key: 'name',
      render: (name) => <Text strong>{name}</Text>,
    },
    {
      title: 'Danh mục',
      dataIndex: 'category',
      key: 'category',
      render: (cat) => <Tag color="geekblue">{cat}</Tag>,
    },
    {
      title: 'Giá',
      dataIndex: 'price',
      key: 'price',
      render: (price) => (
        <Text strong style={{ color: '#10b981' }}>{price.toLocaleString('vi-VN')} ₫</Text>
      ),
    },
    {
      title: 'Hiệu lực từ',
      dataIndex: 'effectiveFrom',
      key: 'effectiveFrom',
    },
    {
      title: 'Mô tả',
      dataIndex: 'description',
      key: 'description',
      render: (d) => <Text type="secondary" style={{ fontSize: 12 }}>{d || '—'}</Text>,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Badge
          status={status === 'ACTIVE' ? 'success' : 'default'}
          text={status === 'ACTIVE' ? 'Đang áp dụng' : 'Ngưng áp dụng'}
        />
      ),
    },
    {
      title: 'Thao tác',
      key: 'actions',
      render: (_, record) => (
        <Space>
          <Tooltip title="Chỉnh sửa">
            <Button size="small" icon={<EditOutlined />} onClick={() => openEdit(record)} />
          </Tooltip>
          <Tooltip title={record.status === 'ACTIVE' ? 'Ngưng áp dụng' : 'Kích hoạt lại'}>
            <Button
              size="small"
              icon={record.status === 'ACTIVE' ? <StopOutlined /> : <CheckCircleOutlined />}
              onClick={() => handleToggleStatus(record)}
              type={record.status === 'ACTIVE' ? 'default' : 'primary'}
            />
          </Tooltip>
          <Popconfirm
            title="Xóa dịch vụ này?"
            description="Hành động này không thể hoàn tác."
            onConfirm={() => handleDelete(record.id)}
            okText="Xóa" cancelText="Hủy" okButtonProps={{ danger: true }}
          >
            <Tooltip title="Xóa dịch vụ">
              <Button size="small" danger icon={<DeleteOutlined />} />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  const activeCount = services.filter((s) => s.status === 'ACTIVE').length
  const inactiveCount = services.filter((s) => s.status === 'INACTIVE').length

  return (
    <div>
      <div className="page-header">
        <div>
          <Title className="page-title" level={3}>Danh mục dịch vụ &amp; Bảng giá</Title>
          <Text type="secondary">Quản lý dịch vụ, giá và thời điểm hiệu lực (NCL-09)</Text>
        </div>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={openCreate}>
          Thêm dịch vụ mới
        </Button>
      </div>

      <Alert
        type="info"
        showIcon
        message="Giá dịch vụ tại thời điểm lập hóa đơn được lưu cố định — không thay đổi dù bảng giá cập nhật sau."
        style={{ marginBottom: 16 }}
      />

      <div style={{ display: 'flex', gap: 16, marginBottom: 16 }}>
        <Card size="small" style={{ borderRadius: 8, flex: 1, background: '#f0fdf4', border: '1px solid #bbf7d0' }}>
          <Text style={{ color: '#15803d', fontWeight: 600 }}>✓ {activeCount} dịch vụ đang áp dụng</Text>
        </Card>
        {inactiveCount > 0 && (
          <Card size="small" style={{ borderRadius: 8, flex: 1, background: '#f8fafc', border: '1px solid #e2e8f0' }}>
            <Text style={{ color: '#64748b' }}>⊘ {inactiveCount} dịch vụ ngưng áp dụng</Text>
          </Card>
        )}
      </div>

      <Card
        title={<Space><SettingOutlined style={{ color: '#6366f1' }} /><span>Danh sách dịch vụ ({services.length})</span></Space>}
        style={{ borderRadius: 12 }}
      >
        <Table columns={columns} dataSource={services} rowKey="id" pagination={{ pageSize: 10 }} />
      </Card>

      <Modal
        title={
          <Space>
            <SettingOutlined style={{ color: '#6366f1' }} />
            <span>{editTarget ? `Chỉnh sửa: ${editTarget.name}` : 'Thêm dịch vụ mới'}</span>
          </Space>
        }
        open={visible}
        onCancel={() => { setVisible(false); form.resetFields() }}
        onOk={handleSave}
        okText={editTarget ? 'Lưu thay đổi' : 'Thêm dịch vụ'}
        cancelText="Hủy"
        destroyOnClose
        width={540}
      >
        <Form form={form} layout="vertical">
          <Form.Item label="Tên dịch vụ" name="name" rules={[{ required: true, message: 'Nhập tên dịch vụ' }]}>
            <Input placeholder="VD: Khám tổng quát" />
          </Form.Item>
          <Form.Item label="Danh mục" name="category" rules={[{ required: true, message: 'Chọn danh mục' }]}>
            <Select placeholder="Chọn danh mục dịch vụ">
              {CATEGORIES.map((c) => <Select.Option key={c} value={c}>{c}</Select.Option>)}
            </Select>
          </Form.Item>
          <Form.Item label="Giá (₫)" name="price" rules={[{ required: true, message: 'Nhập giá' }]}>
            <InputNumber
              min={0}
              style={{ width: '100%' }}
              formatter={(v) => `${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
              parser={(v) => v.replace(/,/g, '')}
              placeholder="VD: 150000"
              addonAfter="₫"
            />
          </Form.Item>
          <Form.Item label="Hiệu lực từ ngày" name="effectiveFrom" rules={[{ required: true, message: 'Chọn ngày hiệu lực' }]}>
            <Input type="date" />
          </Form.Item>
          <Form.Item label="Mô tả" name="description">
            <TextArea rows={2} placeholder="Mô tả ngắn về dịch vụ..." />
          </Form.Item>
          <Form.Item label="Trạng thái" name="status">
            <Select>
              <Select.Option value="ACTIVE">Đang áp dụng</Select.Option>
              <Select.Option value="INACTIVE">Ngưng áp dụng</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default ServicesPage
