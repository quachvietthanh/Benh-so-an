import React, { useCallback, useEffect, useState } from 'react'
import { Alert, Button, Card, Form, Input, InputNumber, List, message, Select, Space, Table, Tag } from 'antd'
import { DeleteOutlined, PlusOutlined, WarningOutlined } from '@ant-design/icons'
import medicalRecordApi from '../api/medicalRecordApi'
import pharmacyApi from '../api/pharmacyApi'

const emptyItem = () => ({ medicineId: undefined, quantity: 1, dosage: '' })
const parseJson = (value, fallback = []) => { try { return typeof value === 'string' ? JSON.parse(value) : (value || fallback) } catch { return fallback } }

function PrescriptionPage() {
  const [medicines, setMedicines] = useState([])
  const [records, setRecords] = useState([])
  const [prescriptions, setPrescriptions] = useState([])
  const [recordId, setRecordId] = useState()
  const [items, setItems] = useState([emptyItem()])
  const [warnings, setWarnings] = useState([])
  const [overrideReason, setOverrideReason] = useState('')
  const [editing, setEditing] = useState(null)
  const [changeReason, setChangeReason] = useState('')
  const [saving, setSaving] = useState(false)

  const load = useCallback(async () => {
    try {
      const [medicineRes, recordRes, prescriptionRes] = await Promise.all([pharmacyApi.medicines(), medicalRecordApi.getAll(), pharmacyApi.prescriptions()])
      setMedicines(medicineRes.data.filter((m) => m.active)); setRecords(recordRes.data); setPrescriptions(prescriptionRes.data)
    } catch (error) { message.error(error.response?.data?.message || 'Không thể tải dữ liệu kê đơn') }
  }, [])
  useEffect(() => { load() }, [load])

  const checkInteractions = useCallback(async (nextItems) => {
    const ids = [...new Set(nextItems.map((item) => item.medicineId).filter(Boolean))]
    if (ids.length < 2) { setWarnings([]); return }
    try { setWarnings((await pharmacyApi.interactions(ids)).data) } catch { setWarnings([]) }
  }, [])

  const updateItem = (index, field, value) => {
    const next = items.map((item, i) => i === index ? { ...item, [field]: value } : item)
    setItems(next); if (field === 'medicineId') checkInteractions(next)
  }

  const validate = () => {
    if (!recordId && !editing) return 'Vui lòng chọn bệnh án đã có chẩn đoán'
    if (!items.length || items.some((item) => !item.medicineId || !item.quantity || !item.dosage.trim())) return 'Mỗi thuốc phải có số lượng và liều dùng'
    if (new Set(items.map((item) => item.medicineId)).size !== items.length) return 'Không được chọn trùng thuốc'
    if (warnings.length && !overrideReason.trim()) return 'Phải nhập lý do chuyên môn khi bỏ qua cảnh báo tương tác'
    return null
  }

  const save = async () => {
    const error = validate(); if (error) { message.error(error); return }
    setSaving(true)
    try {
      if (editing) await pharmacyApi.updatePrescription(editing.id, { items, changeReason, overrideReason })
      else await pharmacyApi.createPrescription({ medicalRecordId: recordId, items, overrideReason })
      message.success(editing ? 'Đã cập nhật đơn và lưu vết thay đổi' : 'Đơn thuốc đã tạo ở trạng thái chờ cấp phát')
      setEditing(null); setRecordId(undefined); setItems([emptyItem()]); setWarnings([]); setOverrideReason(''); setChangeReason(''); await load()
    } catch (err) { message.error(err.response?.data?.message || 'Không thể lưu đơn thuốc') }
    finally { setSaving(false) }
  }

  const beginEdit = (prescription) => {
    const nextItems = parseJson(prescription.items); setEditing(prescription); setItems(nextItems); setOverrideReason(prescription.overrideReason || ''); setChangeReason(''); checkInteractions(nextItems)
  }

  return <div>
    <div className="page-header"><h2 style={{ margin: 0 }}>Kê đơn thuốc và cảnh báo tương tác</h2><Button type="primary" loading={saving} onClick={save}>{editing ? 'Lưu điều chỉnh' : 'Tạo đơn thuốc'}</Button></div>
    <Card title={editing ? `Điều chỉnh ${editing.prescriptionCode}` : 'Đơn thuốc mới'} style={{ marginBottom: 20 }}>
      {!editing && <Form.Item label="Bệnh án đã có chẩn đoán" required><Select showSearch optionFilterProp="label" value={recordId} onChange={setRecordId} options={records.map((r) => ({ value: r.id, label: `${r.recordCode} — ${r.patientName} — ${r.diagnosis}` }))} /></Form.Item>}
      {items.map((item, index) => <Space key={index} style={{ display: 'flex', marginBottom: 10 }} align="start">
        <Select showSearch optionFilterProp="label" placeholder="Chọn thuốc" style={{ width: 280 }} value={item.medicineId} onChange={(value) => updateItem(index, 'medicineId', value)} options={medicines.map((m) => ({ value: m.id, label: `${m.name} (tồn: ${m.stock})` }))} />
        <InputNumber min={1} value={item.quantity} onChange={(value) => updateItem(index, 'quantity', value)} addonBefore="SL" />
        <Input placeholder="Liều dùng/cách dùng" style={{ width: 300 }} value={item.dosage} onChange={(e) => updateItem(index, 'dosage', e.target.value)} />
        <Button danger icon={<DeleteOutlined />} disabled={items.length === 1} onClick={() => { const next=items.filter((_,i)=>i!==index);setItems(next);checkInteractions(next) }} />
      </Space>)}
      <Button icon={<PlusOutlined />} onClick={() => setItems((current) => [...current, emptyItem()])}>Thêm thuốc</Button>
      {!!warnings.length && <Alert style={{ marginTop: 16 }} type="error" showIcon icon={<WarningOutlined />} message="Phát hiện tương tác thuốc" description={<List size="small" dataSource={warnings} renderItem={(w) => <List.Item><Tag color="red">{w.severity}</Tag>{w.description}</List.Item>} />} />}
      {!!warnings.length && <Form.Item label="Lý do chuyên môn khi vẫn tiếp tục" required style={{ marginTop: 12 }}><Input.TextArea value={overrideReason} onChange={(e) => setOverrideReason(e.target.value)} /></Form.Item>}
      {editing && <Form.Item label="Lý do điều chỉnh" required><Input.TextArea value={changeReason} onChange={(e) => setChangeReason(e.target.value)} /></Form.Item>}
    </Card>
    <Card title="Đơn thuốc đã lập"><Table rowKey="id" dataSource={prescriptions} columns={[
      { title: 'Mã đơn', dataIndex: 'prescriptionCode' }, { title: 'Thuốc', dataIndex: 'items', render: (value) => parseJson(value).map((item) => medicines.find((m) => m.id === item.medicineId)?.name || item.medicineId).join(', ') },
      { title: 'Trạng thái', dataIndex: 'status', render: (value) => <Tag color={value === 'PENDING_DISPENSING' ? 'orange' : 'green'}>{value}</Tag> },
      { title: 'Thao tác', render: (_, row) => <Button disabled={row.status !== 'PENDING_DISPENSING'} onClick={() => beginEdit(row)}>Điều chỉnh</Button> },
    ]} /></Card>
  </div>
}
export default PrescriptionPage
