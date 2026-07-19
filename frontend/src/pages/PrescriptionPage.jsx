import React, { useMemo, useState } from 'react'
import { Card, Table, Tag, Button, Alert, Modal, Input, Select, Space, Typography, List } from 'antd'
import { WarningOutlined, MedicineBoxOutlined, PlusOutlined, SaveOutlined } from '@ant-design/icons'
import { getMedicines, getPrescriptions, checkDrugInteractions } from '../services/mockDataService'

const { Option } = Select
const { TextArea } = Input
const { Title, Text } = Typography

function PrescriptionPage() {
  const [medicines] = useState(getMedicines())
  const [prescriptions] = useState(getPrescriptions())
  const [selectedMedicineIds, setSelectedMedicineIds] = useState(['med1', 'med2'])
  const [reasonVisible, setReasonVisible] = useState(false)

  const warnings = useMemo(() => checkDrugInteractions(selectedMedicineIds), [selectedMedicineIds])
  const hasInteractionWarning = warnings.length > 0

  const columns = [
    { title: 'Thuốc', dataIndex: 'name', key: 'name', render: (text) => <Text strong>{text}</Text> },
    { title: 'Nhóm', dataIndex: 'category', key: 'category', render: (text) => <Tag color="blue">{text}</Tag> },
    { title: 'Tồn kho', dataIndex: 'stock', key: 'stock', render: (stock, record) => (
      <Text type={stock <= record.minStock ? 'danger' : 'success'}>{stock}</Text>
    )},
    { title: 'Hạn dùng', dataIndex: 'expiryDate', key: 'expiryDate' },
  ]

  return (
    <div className="animate-fade-in">
      <div className="page-header">
        <div>
          <Title className="page-title" level={3}>Kê đơn thuốc</Title>
          <Text type="secondary">Quản lý kê đơn và kiểm tra tương tác thuốc tự động</Text>
        </div>
        <Button type="primary" icon={<SaveOutlined />} size="large" onClick={() => setReasonVisible(true)}>Lưu đơn thuốc</Button>
      </div>

      <Alert 
        type="info" 
        showIcon 
        message="Hệ thống tự động kiểm tra tương tác thuốc khi kê đơn dựa trên CSDL Dược Quốc Gia." 
        style={{ marginBottom: 24, borderRadius: 8 }} 
      />

      <Card title={<Space><MedicineBoxOutlined style={{ color: '#0ea5e9' }} /><span>Chọn thuốc kê đơn</span></Space>} style={{ borderRadius: 12, marginBottom: 24, border: 'none', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
        <Select 
          mode="multiple" 
          value={selectedMedicineIds} 
          onChange={setSelectedMedicineIds} 
          style={{ width: '100%' }}
          placeholder="Tìm và chọn thuốc..."
          size="large"
          optionLabelProp="label"
        >
          {medicines.map((medicine) => (
            <Option key={medicine.id} value={medicine.id} label={medicine.name}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span>{medicine.name}</span>
                <span style={{ color: '#999', fontSize: 12 }}>Tồn: {medicine.stock}</span>
              </div>
            </Option>
          ))}
        </Select>

        {hasInteractionWarning && (
          <div style={{ marginTop: 24 }}>
            <Alert 
              message={<span style={{ fontWeight: 600, color: '#cf1322' }}>Phát hiện tương tác thuốc!</span>}
              description={
                <List
                  dataSource={warnings}
                  renderItem={item => (
                    <List.Item style={{ padding: '8px 0', border: 'none' }}>
                      <div>
                        <Tag color="red" style={{ marginRight: 8 }}>{item.severity}</Tag>
                        <Text>{item.description}</Text>
                      </div>
                    </List.Item>
                  )}
                />
              }
              type="error" 
              showIcon 
              icon={<WarningOutlined style={{ fontSize: 24, color: '#cf1322' }} />}
              style={{ borderRadius: 8, backgroundColor: '#fff1f0', border: '1px solid #ffa39e' }}
            />
          </div>
        )}
      </Card>

      <Card title="Danh mục thuốc" style={{ borderRadius: 12, border: 'none', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
        <Table columns={columns} dataSource={medicines} rowKey="id" pagination={{ pageSize: 5 }} />
      </Card>

      <Modal
        title={<div><WarningOutlined style={{ color: '#cf1322', marginRight: 8 }} />Xác nhận lưu đơn thuốc</div>}
        open={reasonVisible}
        onCancel={() => setReasonVisible(false)}
        onOk={() => setReasonVisible(false)}
        okText="Xác nhận lưu"
        cancelText="Hủy bỏ"
        okButtonProps={{ danger: hasInteractionWarning }}
      >
        {hasInteractionWarning ? (
          <>
            <p style={{ color: '#cf1322', fontWeight: 500 }}>Cảnh báo: Đơn thuốc có tương tác nguy hiểm.</p>
            <p>Vui lòng ghi rõ lý do chuyên môn nếu bác sĩ vẫn quyết định kê đơn này:</p>
            <TextArea rows={4} placeholder="Nhập lý do chuyên môn (Bắt buộc)..." />
          </>
        ) : (
          <p>Xác nhận lưu đơn thuốc này cho bệnh nhân?</p>
        )}
      </Modal>
    </div>
  )
}

export default PrescriptionPage
