import React, { useCallback, useEffect, useState } from 'react'
import { Alert, Button, Card, Descriptions, Form, Input, List, message, Modal, Select, Space, Table, Tabs, Tag, Upload } from 'antd'
import { EyeOutlined, UploadOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import medicalRecordApi from '../api/medicalRecordApi'
import patientApi from '../api/patientApi'
import { useAuthContext } from '../context/AuthContext'

const testOptions = ['Công thức máu', 'Đường huyết', 'Sinh hóa máu', 'Nước tiểu', 'X-quang', 'Siêu âm', 'CT Scanner', 'MRI']

function MedicalEncounter() {
  const { user } = useAuthContext()
  const isDoctor = user?.roles?.includes('doctor')
  const [form] = Form.useForm()
  const [patients, setPatients] = useState([])
  const [records, setRecords] = useState([])
  const [orders, setOrders] = useState([])
  const [results, setResults] = useState({})
  const [files, setFiles] = useState([])
  const [saving, setSaving] = useState(false)
  const [activeTab, setActiveTab] = useState('current')
  const [viewing, setViewing] = useState(null)

  const loadData = useCallback(async () => {
    try {
      const [patientResponse, recordResponse] = await Promise.all([
        patientApi.getAll({ page: 0, size: 200 }), medicalRecordApi.getAll(),
      ])
      setPatients(patientResponse.data.content || [])
      setRecords(recordResponse.data || [])
    } catch (error) { message.error(error.response?.data?.message || 'Không thể tải dữ liệu bệnh án') }
  }, [])

  useEffect(() => { loadData() }, [loadData])

  const saveRecord = async () => {
    try {
      const values = await form.validateFields()
      setSaving(true)
      const response = await medicalRecordApi.create({
        ...values, clinicalOrders: orders,
        clinicalResults: Object.fromEntries(Object.entries(results).filter(([, value]) => value?.trim())),
      })
      for (const file of files) await medicalRecordApi.attach(response.data.id, file)
      message.success(`Đã lưu bệnh án ${response.data.recordCode}`)
      form.resetFields(); setOrders([]); setResults({}); setFiles([])
      await loadData(); setActiveTab('history')
    } catch (error) {
      if (error?.errorFields) message.error('Vui lòng nhập đủ bệnh nhân, triệu chứng và chẩn đoán')
      else message.error(error.response?.data?.message || 'Không thể lưu bệnh án')
    } finally { setSaving(false) }
  }

  const beforeUpload = (file) => {
    const allowed = ['application/pdf', 'image/jpeg', 'image/png'].includes(file.type)
    if (!allowed) { message.error('Chỉ chấp nhận PDF, JPG hoặc PNG'); return Upload.LIST_IGNORE }
    if (file.size > 10 * 1024 * 1024) { message.error('Tệp không được vượt quá 10 MB'); return Upload.LIST_IGNORE }
    setFiles((current) => [...current, file]); return false
  }

  const downloadAttachment = async (file) => {
    try {
      const response = await medicalRecordApi.downloadAttachment(file.id)
      const url = URL.createObjectURL(response.data)
      const link = document.createElement('a')
      link.href = url; link.download = file.fileName; link.click()
      URL.revokeObjectURL(url)
    } catch { message.error('Không thể tải tệp đính kèm') }
  }

  const openRecord = async (record) => {
    try { setViewing((await medicalRecordApi.getById(record.id)).data) }
    catch (error) { message.error(error.response?.data?.message || 'Không thể mở bệnh án') }
  }

  const columns = [
    { title: 'Mã bệnh án', dataIndex: 'recordCode', render: (value) => <Tag color="green">{value}</Tag> },
    { title: 'Bệnh nhân', dataIndex: 'patientName' },
    { title: 'Chẩn đoán', dataIndex: 'diagnosis' },
    { title: 'Bác sĩ', dataIndex: 'doctorName' },
    { title: 'Ngày lập', dataIndex: 'createdAt', render: (value) => dayjs(value).format('HH:mm DD/MM/YYYY') },
    { title: 'Trạng thái', dataIndex: 'status', render: (value) => <Tag color="green">{value}</Tag> },
    { title: '', render: (_, record) => <Button icon={<EyeOutlined />} onClick={() => openRecord(record)}>Xem</Button> },
  ]

  return <div>
    <div className="page-header"><h2 style={{ margin: 0 }}>Khám bệnh và bệnh án điện tử</h2>{isDoctor && <Button type="primary" loading={saving} onClick={saveRecord}>Lưu bệnh án</Button>}</div>
    <Alert showIcon type="info" message="Bệnh án được lưu theo từng lượt khám; chỉ bác sĩ được ghi nội dung, chẩn đoán, chỉ định và kết quả." style={{ marginBottom: 16 }} />
    <Tabs activeKey={activeTab} onChange={setActiveTab} items={[
      { key: 'current', label: 'Ghi bệnh án', children: <Card><Form form={form} layout="vertical" disabled={!isDoctor}>
        <Form.Item name="patientId" label="Bệnh nhân" rules={[{ required: true, message: 'Chọn bệnh nhân' }]}><Select showSearch optionFilterProp="label" options={patients.map((p) => ({ value: p.id, label: `${p.fullName} (${p.patientCode})` }))} /></Form.Item>
        <Form.Item name="symptoms" label="Triệu chứng/Lý do khám" rules={[{ required: true, message: 'Nhập triệu chứng' }]}><Input.TextArea rows={3} /></Form.Item>
        <Form.Item name="examinationNote" label="Khám lâm sàng và diễn biến"><Input.TextArea rows={4} placeholder="Dấu hiệu sinh tồn, kết quả khám, diễn biến..." /></Form.Item>
        <Form.Item name="diagnosis" label="Chẩn đoán" rules={[{ required: true, message: 'Nhập chẩn đoán' }]}><Input.TextArea rows={2} /></Form.Item>
        <Form.Item name="treatmentPlan" label="Hướng điều trị/Chỉ định"><Input.TextArea rows={3} /></Form.Item>
        <Form.Item label="Chỉ định cận lâm sàng"><Select mode="multiple" value={orders} onChange={(values) => { setOrders(values); setResults((current) => Object.fromEntries(Object.entries(current).filter(([key]) => values.includes(key)))) }} options={testOptions.map((value) => ({ value, label: value }))} /></Form.Item>
        {!!orders.length && <Form.Item label="Kết quả cận lâm sàng"><Space direction="vertical" style={{ width: '100%' }}>{orders.map((order) => <Input key={order} addonBefore={order} value={results[order] || ''} placeholder="Nhập kết quả và đơn vị" onChange={(event) => setResults((current) => ({ ...current, [order]: event.target.value }))} />)}</Space></Form.Item>}
        <Form.Item label="Tệp kết quả (PDF/JPG/PNG, tối đa 10 MB)"><Upload beforeUpload={beforeUpload} fileList={files} onRemove={(file) => setFiles((current) => current.filter((item) => item.uid !== file.uid))}><Button icon={<UploadOutlined />}>Chọn tệp</Button></Upload></Form.Item>
      </Form></Card> },
      { key: 'history', label: `Lịch sử bệnh án (${records.length})`, children: <Card><Table rowKey="id" columns={columns} dataSource={records} pagination={{ pageSize: 10 }} /></Card> },
    ]} />

    <Modal title={`Bệnh án ${viewing?.recordCode || ''}`} open={!!viewing} onCancel={() => setViewing(null)} footer={<Button onClick={() => setViewing(null)}>Đóng</Button>} width={760}>
      {viewing && <>
        <Descriptions bordered column={1} size="small">
          <Descriptions.Item label="Bệnh nhân">{viewing.patientName}</Descriptions.Item>
          <Descriptions.Item label="Bác sĩ">{viewing.doctorName}</Descriptions.Item>
          <Descriptions.Item label="Triệu chứng">{viewing.symptoms}</Descriptions.Item>
          <Descriptions.Item label="Khám lâm sàng">{viewing.examinationNote || '---'}</Descriptions.Item>
          <Descriptions.Item label="Chẩn đoán">{viewing.diagnosis}</Descriptions.Item>
          <Descriptions.Item label="Hướng điều trị">{viewing.treatmentPlan || '---'}</Descriptions.Item>
          <Descriptions.Item label="Chỉ định">{viewing.clinicalOrders?.join(', ') || 'Không có'}</Descriptions.Item>
          <Descriptions.Item label="Kết quả">{Object.entries(viewing.clinicalResults || {}).map(([key, value]) => `${key}: ${value}`).join(' | ') || 'Chưa có'}</Descriptions.Item>
        </Descriptions>
        <List header="Tệp đính kèm" dataSource={viewing.attachments || []} locale={{ emptyText: 'Không có tệp' }} renderItem={(file) => <List.Item><Button type="link" onClick={() => downloadAttachment(file)}>{file.fileName}</Button></List.Item>} />
      </>}
    </Modal>
  </div>
}

export default MedicalEncounter
