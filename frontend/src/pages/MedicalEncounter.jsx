import React, { useState } from 'react'
import {
  Card, Form, Input, Select, Button, Typography, Alert, Tabs, Table, Tag,
  Upload, message, Modal, Space, List, Descriptions, Collapse, Row, Col, Radio, DatePicker, InputNumber,
} from 'antd'
import { UploadOutlined, EyeOutlined } from '@ant-design/icons'
import { getPatients } from '../services/mockDataService'
import { useAuthContext } from '../context/AuthContext'

const { Title } = Typography
const { TextArea } = Input

const ALLOWED_FILE_TYPES = ['.pdf', '.jpg', '.jpeg', '.png']
const numberRegex = /^-?\d+(\.\d+)?$/

let nextRecordSeq = 1000

function MedicalEncounter() {
  const { user } = useAuthContext()
  const isDoctor = true // Đã đăng nhập là có quyền thao tác, không giới hạn theo vai trò bác sĩ nữa

  // Lịch sử khám bắt đầu trống — chỉ hiện các bệnh án do người dùng tự lưu trong phiên làm việc này.
  // TODO: khi có backend thật, thay bằng gọi API lấy đúng lịch sử khám đã lưu trước đó.
  const [records, setRecords] = useState([])
  const patients = getPatients()

  const [activeTab, setActiveTab] = useState('current')
  const [form] = Form.useForm()
  const selectedPatientId = Form.useWatch('patient', form)
  const selectedPatientInfo = patients.find((p) => p.id === selectedPatientId)
  const [attachments, setAttachments] = useState([])
  const [selectedTests, setSelectedTests] = useState([])
  const [orderedTests, setOrderedTests] = useState([])
  const [testResults, setTestResults] = useState({})
  const [accessLog, setAccessLog] = useState([])

  const [viewOpen, setViewOpen] = useState(false)
  const [viewingRecord, setViewingRecord] = useState(null)
  const [amendOpen, setAmendOpen] = useState(false)
  const [amendReason, setAmendReason] = useState('')

  const logEntry = (action) => {
    setAccessLog((prev) => [
      { time: new Date().toLocaleString('vi-VN'), user: user?.fullName || 'Người dùng', action },
      ...prev,
    ])
  }

  // --- NCL-04-CN-001 & CN-002: Lưu bệnh án, sau đó chuyển sang Lịch sử khám ---
  const handleSaveClick = async () => {
    if (!isDoctor) {
      message.error('Chỉ bác sĩ mới có quyền ghi chẩn đoán và lưu bệnh án')
      return
    }
    try {
      const values = await form.validateFields(['patient', 'symptoms', 'diagnosis', 'department', 'admissionDateTime'])
      const patientInfo = patients.find((p) => p.id === values.patient)
      const allValues = form.getFieldsValue()

      nextRecordSeq += 1
      const newRecord = {
        id: `m-${Date.now()}`,
        recordCode: `BA-${String(nextRecordSeq).padStart(4, '0')}`,
        patientId: values.patient,
        patientName: patientInfo?.fullName || '—',
        symptoms: values.symptoms,
        progress: values.progress,
        diagnosis: values.diagnosis,
        orderedTests,
        testResults,
        attachments,
        doctorName: user?.fullName || 'Bác sĩ',
        status: 'COMPLETED',
        createdAt: new Date().toISOString(),
        // Phần II. Quản lý người bệnh
        management: {
          admissionDateTime: allValues.admissionDateTime?.format('HH:mm DD/MM/YYYY'),
          admissionType: allValues.admissionType,
          referralSource: allValues.referralSource,
          admissionCount: allValues.admissionCount,
          department: allValues.department,
          bedNumber: allValues.bedNumber,
          dischargeDateTime: allValues.dischargeDateTime?.format('HH:mm DD/MM/YYYY'),
          dischargeType: allValues.dischargeType,
          totalTreatmentDays: allValues.totalTreatmentDays,
        },
      }

      // TODO: gọi API lưu bệnh án thật khi có backend, thay vì chỉ lưu vào state
      setRecords((prev) => [newRecord, ...prev])
      logEntry(`đã lưu bệnh án ${newRecord.recordCode} (chẩn đoán: ${values.diagnosis})`)
      message.success('Đã lưu bệnh án — chuyển vào Lịch sử khám')

      // Reset để bắt đầu lượt khám mới
      form.resetFields()
      setSelectedTests([])
      setOrderedTests([])
      setTestResults({})
      setAttachments([])
      setActiveTab('history')
    } catch {
      message.error('Vui lòng chọn bệnh nhân và nhập đầy đủ triệu chứng, chẩn đoán trước khi lưu')
    }
  }

  // --- NCL-04-CN-005: Lưu chỉ định cận lâm sàng ---
  const handleSaveOrders = () => {
    if (!isDoctor) {
      message.error('Chỉ bác sĩ mới có quyền chỉ định cận lâm sàng')
      return
    }
    if (selectedTests.length === 0) {
      message.error('Vui lòng chọn ít nhất một loại cận lâm sàng')
      return
    }
    setOrderedTests(selectedTests)
    logEntry(`đã chỉ định cận lâm sàng: ${selectedTests.join(', ')}`)
    message.success('Đã lưu chỉ định cận lâm sàng')
  }

  // --- NCL-04-CN-006: Nhập kết quả cận lâm sàng ---
  const handleSaveResult = (testName) => {
    const value = testResults[testName]?.draft ?? ''
    if (!numberRegex.test(value)) {
      message.error(`Kết quả "${testName}" phải là giá trị số hợp lệ`)
      return
    }
    setTestResults((prev) => ({
      ...prev,
      [testName]: { value, draft: value, edited: !!prev[testName]?.value },
    }))
    logEntry(`đã nhập kết quả cận lâm sàng: ${testName} = ${value}`)
    message.success(`Đã lưu kết quả ${testName}`)
  }

  // --- NCL-04-CN-003: Đính kèm kết quả (tập tin) ---
  const beforeUpload = (file) => {
    if (!isDoctor) {
      message.error('Chỉ bác sĩ mới có quyền đính kèm kết quả')
      return Upload.LIST_IGNORE
    }
    const ext = '.' + file.name.split('.').pop().toLowerCase()
    if (!ALLOWED_FILE_TYPES.includes(ext)) {
      message.error(`Định dạng không hợp lệ. Chỉ chấp nhận: ${ALLOWED_FILE_TYPES.join(', ')}`)
      return Upload.LIST_IGNORE
    }
    setAttachments((prev) => [...prev, file.name])
    logEntry(`đã đính kèm tập tin kết quả: ${file.name}`)
    message.success(`Đã đính kèm ${file.name}`)
    return Upload.LIST_IGNORE
  }

  // --- NCL-04-CN-004: Xem chi tiết bệnh án từ Lịch sử khám ---
  const openView = (record) => {
    setViewingRecord(record)
    setViewOpen(true)
    logEntry(`đã xem bệnh án ${record.recordCode}`)
  }

  // --- NCL-04-CN-001-TC-03: Bổ sung bệnh án đã khóa (COMPLETED) ---
  const handleAmendConfirm = () => {
    if (!amendReason.trim()) {
      message.error('Vui lòng nhập lý do bổ sung')
      return
    }
    setRecords((prev) =>
      prev.map((r) =>
        r.id === viewingRecord.id
          ? { ...r, amendments: [...(r.amendments || []), { time: new Date().toLocaleString('vi-VN'), reason: amendReason }] }
          : r
      )
    )
    logEntry(`bổ sung bệnh án đã khóa ${viewingRecord.recordCode} — lý do: ${amendReason}`)
    message.success('Đã lưu bổ sung có ghi lý do')
    setAmendOpen(false)
    setAmendReason('')
  }

  const historyColumns = [
    { title: 'Mã hồ sơ', dataIndex: 'recordCode', key: 'recordCode', render: (text) => <Tag color="green">{text}</Tag> },
    { title: 'Bệnh nhân', dataIndex: 'patientName', key: 'patientName' },
    { title: 'Chẩn đoán', dataIndex: 'diagnosis', key: 'diagnosis' },
    { title: 'Bác sĩ', dataIndex: 'doctorName', key: 'doctorName' },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      render: (status) => <Tag color={status === 'COMPLETED' ? 'green' : 'orange'}>{status}</Tag>,
    },
    {
      title: '',
      key: 'actions',
      render: (_, record) => (
        <Button size="small" icon={<EyeOutlined />} onClick={() => openView(record)}>Xem</Button>
      ),
    },
  ]

  return (
    <div>
      <div className="page-header">
        <Title level={4} style={{ margin: 0 }}>Bệnh án điện tử</Title>
        <Button type="primary" onClick={handleSaveClick}>Lưu bệnh án</Button>
      </div>

      <Alert
        type="warning"
        showIcon
        message="Tất cả thao tác xem/sửa bệnh án sẽ được ghi nhật ký truy cập. Bệnh án kết thúc lượt khám sẽ chuyển sang chế độ chỉ đọc."
        style={{ marginBottom: 16 }}
      />

      {!isDoctor && (
        <Alert
          type="error"
          showIcon
          message="Tài khoản hiện tại không có quyền thao tác — chỉ có thể xem."
          style={{ marginBottom: 16 }}
        />
      )}

      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        items={[
          {
            key: 'current',
            label: 'Lượt khám hiện tại',
            children: (
              <Card style={{ borderRadius: 12 }}>
                <Form form={form} layout="vertical">
                  <Form.Item
                    label="Bệnh nhân"
                    name="patient"
                    rules={[{ required: true, message: 'Vui lòng chọn bệnh nhân' }]}
                  >
                    <Select
                      showSearch
                      placeholder="Chọn bệnh nhân"
                      disabled={!isDoctor}
                      options={patients.map((p) => ({ value: p.id, label: `${p.fullName} (${p.patientCode})` }))}
                      filterOption={(input, option) => option.label.toLowerCase().includes(input.toLowerCase())}
                    />
                  </Form.Item>

                  <Collapse
                    defaultActiveKey={['hanhchinh', 'quanly']}
                    style={{ marginBottom: 24 }}
                    items={[
                      {
                        key: 'hanhchinh',
                        label: 'I. Hành chính',
                        children: selectedPatientInfo ? (
                          <Descriptions bordered size="small" column={2}>
                            <Descriptions.Item label="Họ và tên" span={2}>{selectedPatientInfo.fullName}</Descriptions.Item>
                            <Descriptions.Item label="Sinh ngày">{selectedPatientInfo.dateOfBirth || '—'}</Descriptions.Item>
                            <Descriptions.Item label="Giới">{selectedPatientInfo.gender === 'male' ? 'Nam' : selectedPatientInfo.gender === 'female' ? 'Nữ' : '—'}</Descriptions.Item>
                            <Descriptions.Item label="Địa chỉ" span={2}>{selectedPatientInfo.address || '—'}</Descriptions.Item>
                            <Descriptions.Item label="Số CMND/CCCD">{selectedPatientInfo.identityNumber || '—'}</Descriptions.Item>
                            <Descriptions.Item label="Đối tượng / Số thẻ BHYT">{selectedPatientInfo.healthInsuranceCode || 'Không có BHYT'}</Descriptions.Item>
                            <Descriptions.Item label="Điện thoại liên hệ" span={2}>{selectedPatientInfo.phoneNumber || '—'}</Descriptions.Item>
                            <Descriptions.Item label="Người nhà khi cần báo tin" span={2}>{selectedPatientInfo.emergencyContact || '—'}</Descriptions.Item>
                          </Descriptions>
                        ) : (
                          <span style={{ color: '#999' }}>Chọn bệnh nhân ở trên để tự động hiện thông tin hành chính từ hồ sơ.</span>
                        ),
                      },
                      {
                        key: 'quanly',
                        label: 'II. Quản lý người bệnh',
                        children: (
                          <>
                            <Row gutter={16}>
                              <Col span={12}>
                                <Form.Item
                                  name="admissionDateTime"
                                  label="Vào viện lúc"
                                  rules={[{ required: true, message: 'Chọn thời điểm vào viện' }]}
                                >
                                  <DatePicker showTime format="HH:mm DD/MM/YYYY" style={{ width: '100%' }} disabled={!isDoctor} />
                                </Form.Item>
                              </Col>
                              <Col span={12}>
                                <Form.Item name="admissionCount" label="Vào viện do bệnh này lần thứ">
                                  <InputNumber min={1} style={{ width: '100%' }} disabled={!isDoctor} />
                                </Form.Item>
                              </Col>
                            </Row>

                            <Form.Item name="admissionType" label="Trực tiếp vào viện">
                              <Radio.Group disabled={!isDoctor}>
                                <Radio value="Cấp cứu">Cấp cứu</Radio>
                                <Radio value="Khám bệnh ngoại trú">Khám bệnh ngoại trú (KKB)</Radio>
                                <Radio value="Khoa điều trị">Khoa điều trị</Radio>
                              </Radio.Group>
                            </Form.Item>

                            <Form.Item name="referralSource" label="Nơi giới thiệu">
                              <Radio.Group disabled={!isDoctor}>
                                <Radio value="Cơ quan y tế">Cơ quan y tế</Radio>
                                <Radio value="Tự đến">Tự đến</Radio>
                                <Radio value="Khác">Khác</Radio>
                              </Radio.Group>
                            </Form.Item>

                            <Row gutter={16}>
                              <Col span={12}>
                                <Form.Item
                                  name="department"
                                  label="Khoa điều trị"
                                  rules={[{ required: true, message: 'Nhập khoa điều trị' }]}
                                >
                                  <Input disabled={!isDoctor} placeholder="Ví dụ: Nội tổng hợp" />
                                </Form.Item>
                              </Col>
                              <Col span={12}>
                                <Form.Item name="bedNumber" label="Số giường">
                                  <Input disabled={!isDoctor} />
                                </Form.Item>
                              </Col>
                            </Row>

                            <Row gutter={16}>
                              <Col span={12}>
                                <Form.Item name="dischargeDateTime" label="Ra viện lúc">
                                  <DatePicker showTime format="HH:mm DD/MM/YYYY" style={{ width: '100%' }} disabled={!isDoctor} />
                                </Form.Item>
                              </Col>
                              <Col span={12}>
                                <Form.Item name="totalTreatmentDays" label="Tổng số ngày điều trị">
                                  <InputNumber min={0} style={{ width: '100%' }} disabled={!isDoctor} />
                                </Form.Item>
                              </Col>
                            </Row>

                            <Form.Item name="dischargeType" label="Hình thức ra viện">
                              <Radio.Group disabled={!isDoctor}>
                                <Radio value="Ra viện">Ra viện</Radio>
                                <Radio value="Xin về">Xin về</Radio>
                                <Radio value="Bỏ về">Bỏ về</Radio>
                                <Radio value="Đưa về">Đưa về</Radio>
                              </Radio.Group>
                            </Form.Item>
                          </>
                        ),
                      },
                    ]}
                  />

                  <Title level={5}>III. Chẩn đoán và chuyên môn khám bệnh</Title>
                  <Form.Item
                    label="Triệu chứng"
                    name="symptoms"
                    rules={[{ required: true, message: 'Vui lòng nhập triệu chứng' }]}
                  >
                    <TextArea rows={3} disabled={!isDoctor} />
                  </Form.Item>
                  <Form.Item label="Khám và diễn tiến" name="progress">
                    <TextArea rows={3} disabled={!isDoctor} />
                  </Form.Item>
                  <Form.Item
                    label="Chẩn đoán"
                    name="diagnosis"
                    rules={[{ required: true, message: 'Vui lòng nhập chẩn đoán' }]}
                  >
                    <Input disabled={!isDoctor} />
                  </Form.Item>

                  <Form.Item label="Chỉ định cận lâm sàng">
                    <Space.Compact style={{ width: '100%' }}>
                      <Select
                        mode="multiple"
                        value={selectedTests}
                        onChange={setSelectedTests}
                        disabled={!isDoctor}
                        style={{ width: '100%' }}
                        options={[
                          { value: 'Xét nghiệm máu', label: 'Xét nghiệm máu' },
                          { value: 'Siêu âm', label: 'Siêu âm' },
                          { value: 'X-quang', label: 'X-quang' },
                        ]}
                      />
                      <Button onClick={handleSaveOrders} disabled={!isDoctor}>Lưu chỉ định</Button>
                    </Space.Compact>
                  </Form.Item>

                  {orderedTests.length > 0 && (
                    <Form.Item label="Kết quả cận lâm sàng">
                      <Space direction="vertical" style={{ width: '100%' }}>
                        {orderedTests.map((t) => (
                          <Space.Compact key={t} style={{ width: '100%' }}>
                            <Input
                              addonBefore={t}
                              placeholder="Nhập giá trị số"
                              value={testResults[t]?.draft ?? ''}
                              onChange={(e) =>
                                setTestResults((prev) => ({ ...prev, [t]: { ...prev[t], draft: e.target.value } }))
                              }
                              disabled={!isDoctor}
                            />
                            <Button onClick={() => handleSaveResult(t)} disabled={!isDoctor}>
                              {testResults[t]?.value ? 'Sửa kết quả' : 'Lưu kết quả'}
                            </Button>
                          </Space.Compact>
                        ))}
                      </Space>
                    </Form.Item>
                  )}

                  <Form.Item label="Đính kèm kết quả (PDF/JPG/PNG)">
                    <Upload beforeUpload={beforeUpload} showUploadList={false} disabled={!isDoctor}>
                      <Button icon={<UploadOutlined />} disabled={!isDoctor}>Tải tập tin kết quả</Button>
                    </Upload>
                    {attachments.length > 0 && (
                      <List
                        size="small"
                        style={{ marginTop: 8 }}
                        dataSource={attachments}
                        renderItem={(name) => <List.Item>{name}</List.Item>}
                      />
                    )}
                  </Form.Item>
                </Form>
              </Card>
            ),
          },
          {
            key: 'history',
            label: `Lịch sử khám (${records.length})`,
            children: (
              <Card style={{ borderRadius: 12 }}>
                <Table columns={historyColumns} dataSource={records} rowKey="id" pagination={{ pageSize: 8 }} />
              </Card>
            ),
          },
        ]}
      />

      {accessLog.length > 0 && (
        <Card title="Nhật ký truy cập bệnh án" style={{ borderRadius: 12, marginTop: 16 }}>
          <List
            dataSource={accessLog}
            renderItem={(item) => <List.Item>{item.time} — {item.user} {item.action}</List.Item>}
          />
        </Card>
      )}

      {/* Modal xem chi tiết bệnh án từ Lịch sử khám */}
      <Modal
        title={`Bệnh án ${viewingRecord?.recordCode ?? ''}`}
        open={viewOpen}
        onCancel={() => setViewOpen(false)}
        footer={
          viewingRecord?.status === 'COMPLETED'
            ? [
                <Button key="amend" onClick={() => setAmendOpen(true)}>Bổ sung ghi chú</Button>,
                <Button key="close" type="primary" onClick={() => setViewOpen(false)}>Đóng</Button>,
              ]
            : [<Button key="close" type="primary" onClick={() => setViewOpen(false)}>Đóng</Button>]
        }
        width={640}
      >
        {viewingRecord && (
          <>
            <Descriptions bordered column={1} size="small" title="II. Quản lý người bệnh">
              <Descriptions.Item label="Vào viện lúc">{viewingRecord.management?.admissionDateTime || '—'}</Descriptions.Item>
              <Descriptions.Item label="Trực tiếp vào viện">{viewingRecord.management?.admissionType || '—'}</Descriptions.Item>
              <Descriptions.Item label="Nơi giới thiệu">{viewingRecord.management?.referralSource || '—'}</Descriptions.Item>
              <Descriptions.Item label="Vào viện do bệnh này lần thứ">{viewingRecord.management?.admissionCount ?? '—'}</Descriptions.Item>
              <Descriptions.Item label="Khoa điều trị">{viewingRecord.management?.department || '—'}</Descriptions.Item>
              <Descriptions.Item label="Số giường">{viewingRecord.management?.bedNumber || '—'}</Descriptions.Item>
              <Descriptions.Item label="Ra viện lúc">{viewingRecord.management?.dischargeDateTime || 'Chưa ra viện'}</Descriptions.Item>
              <Descriptions.Item label="Hình thức ra viện">{viewingRecord.management?.dischargeType || '—'}</Descriptions.Item>
              <Descriptions.Item label="Tổng số ngày điều trị">{viewingRecord.management?.totalTreatmentDays ?? '—'}</Descriptions.Item>
            </Descriptions>

            <Descriptions bordered column={1} size="small" title="III. Chẩn đoán và chuyên môn" style={{ marginTop: 16 }}>
              <Descriptions.Item label="Bệnh nhân">{viewingRecord.patientName || '—'}</Descriptions.Item>
              <Descriptions.Item label="Triệu chứng">{viewingRecord.symptoms || 'Không có dữ liệu'}</Descriptions.Item>
              <Descriptions.Item label="Khám và diễn tiến">{viewingRecord.progress || 'Không có dữ liệu'}</Descriptions.Item>
              <Descriptions.Item label="Chẩn đoán">{viewingRecord.diagnosis}</Descriptions.Item>
              <Descriptions.Item label="Chỉ định cận lâm sàng">
                {viewingRecord.orderedTests?.length ? viewingRecord.orderedTests.join(', ') : 'Không có'}
              </Descriptions.Item>
              <Descriptions.Item label="Kết quả cận lâm sàng">
                {viewingRecord.testResults && Object.keys(viewingRecord.testResults).length
                  ? Object.entries(viewingRecord.testResults).map(([k, v]) => `${k}: ${v.value}`).join(' | ')
                  : 'Chưa có kết quả'}
              </Descriptions.Item>
              <Descriptions.Item label="Tập tin đính kèm">
                {viewingRecord.attachments?.length ? viewingRecord.attachments.join(', ') : 'Không có'}
              </Descriptions.Item>
              <Descriptions.Item label="Bác sĩ">{viewingRecord.doctorName}</Descriptions.Item>
              <Descriptions.Item label="Trạng thái">{viewingRecord.status}</Descriptions.Item>
            </Descriptions>

            {viewingRecord.amendments?.length > 0 && (
              <div style={{ marginTop: 16 }}>
                <strong>Ghi chú bổ sung:</strong>
                <List
                  size="small"
                  dataSource={viewingRecord.amendments}
                  renderItem={(a) => <List.Item>{a.time} — {a.reason}</List.Item>}
                />
              </div>
            )}
          </>
        )}
      </Modal>

      <Modal
        title="Bổ sung bệnh án đã khóa"
        open={amendOpen}
        onCancel={() => setAmendOpen(false)}
        onOk={handleAmendConfirm}
        okText="Xác nhận bổ sung"
        cancelText="Hủy"
      >
        <p>Bệnh án đã kết thúc lượt khám. Vui lòng nêu rõ lý do bổ sung:</p>
        <TextArea rows={3} value={amendReason} onChange={(e) => setAmendReason(e.target.value)} />
      </Modal>
    </div>
  )
}

export default MedicalEncounter