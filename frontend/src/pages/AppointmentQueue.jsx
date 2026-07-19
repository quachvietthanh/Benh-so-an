import React, { useState, useMemo } from 'react'
import {
  Table, Button, Modal, Form, Input, Select, DatePicker,
  message, Space, Tag, Empty, Card, List, Collapse,
} from 'antd'
import { PlusOutlined, CloseCircleOutlined, UserSwitchOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { getPatients, getUsers } from '../services/mockDataService'

const initialAppointments = [
  {
    id: 'LH-001',
    patient: 'Nguyễn Thị An',
    doctor: 'BS. Trần Văn Bình',
    time: dayjs().add(1, 'hour'),
    status: 'đã đặt', // đã đặt | đã hủy | không đến | đã check-in | hoàn tất
  },
  {
    id: 'LH-002',
    patient: 'Lê Văn Cường',
    doctor: 'BS. Nguyễn Thị Hoa',
    time: dayjs().subtract(1, 'hour'),
    status: 'đã đặt',
  },
]

let nextId = 3

const statusColor = {
  'đã đặt': 'blue',
  'đã hủy': 'default',
  'không đến': 'red',
  'đã check-in': 'green',
  'hoàn tất': 'purple',
}

// Trạng thái coi là "đã kết thúc vòng đời" -> chuyển sang lịch sử
const HISTORY_STATUSES = ['đã hủy', 'không đến', 'hoàn tất']

function AppointmentQueue() {
  const [appointments, setAppointments] = useState(initialAppointments)
  const [queue, setQueue] = useState([])

  const [bookOpen, setBookOpen] = useState(false)
  const [cancelOpen, setCancelOpen] = useState(false)
  const [cancelingAppt, setCancelingAppt] = useState(null)

  const [bookForm] = Form.useForm()
  const [cancelForm] = Form.useForm()

  // --- Lấy danh sách bệnh nhân / bác sĩ thật từ hệ thống, không gõ cứng nữa ---
  const patientOptions = useMemo(
    () => getPatients().map((p) => ({ value: p.fullName, label: `${p.fullName} (${p.patientCode})` })),
    []
  )
  const doctorOptions = useMemo(
    () =>
      getUsers()
        .filter((u) => u.roles?.includes('doctor'))
        .map((d) => ({ value: d.fullName, label: d.fullName })),
    []
  )

  const activeAppointments = appointments.filter((a) => !HISTORY_STATUSES.includes(a.status))
  const historyAppointments = appointments.filter((a) => HISTORY_STATUSES.includes(a.status))

  // --- NCL-03-CN-001: Đặt lịch hẹn ---
  const openBook = () => {
    bookForm.resetFields()
    setBookOpen(true)
  }

  const handleBook = (values) => {
    const chosenTime = values.time
    if (chosenTime.isBefore(dayjs())) {
      message.error('Không thể đặt lịch vào thời điểm đã qua')
      return
    }
    const conflict = appointments.find(
      (a) =>
        a.doctor === values.doctor &&
        a.status !== 'đã hủy' &&
        Math.abs(a.time.diff(chosenTime, 'minute')) < 30
    )
    if (conflict) {
      message.error(`Bác sĩ ${values.doctor} đã có lịch hẹn trùng khung giờ này`)
      return
    }
    const newAppt = {
      id: `LH-${String(nextId).padStart(3, '0')}`,
      patient: values.patient,
      doctor: values.doctor,
      time: chosenTime,
      status: 'đã đặt',
    }
    nextId += 1
    setAppointments((prev) => [...prev, newAppt])
    message.success('Đặt lịch hẹn thành công')
    setBookOpen(false)
  }

  // --- NCL-03-CN-002: Hủy lịch hẹn ---
  const openCancel = (appt) => {
    if (appt.status !== 'đã đặt') {
      message.warning('Chỉ có thể hủy lịch hẹn đang ở trạng thái đã đặt')
      return
    }
    cancelForm.resetFields()
    setCancelingAppt(appt)
    setCancelOpen(true)
  }

  const handleCancel = (values) => {
    setAppointments((prev) =>
      prev.map((a) => (a.id === cancelingAppt.id ? { ...a, status: 'đã hủy', cancelReason: values.reason } : a))
    )
    message.success(`Đã hủy lịch hẹn ${cancelingAppt.id} — chuyển vào lịch sử`)
    setCancelOpen(false)
  }

  // --- NCL-03-CN-003: Đánh dấu không đến ---
  const markNoShow = (appt) => {
    if (appt.status !== 'đã đặt') {
      message.warning('Không thể đánh dấu không đến ở trạng thái hiện tại (có thể đã check-in)')
      return
    }
    if (appt.time.isAfter(dayjs())) {
      message.warning('Chưa quá giờ hẹn, chưa thể đánh dấu không đến')
      return
    }
    setAppointments((prev) => prev.map((a) => (a.id === appt.id ? { ...a, status: 'không đến' } : a)))
    message.success(`Đã đánh dấu không đến — chuyển vào lịch sử`)
  }

  // --- NCL-03-CN-004: Check-in hàng đợi ---
  const checkIn = (appt) => {
    if (appt.status !== 'đã đặt') {
      message.warning('Chỉ có thể check-in lịch hẹn đang ở trạng thái đã đặt')
      return
    }
    if (queue.find((q) => q.id === appt.id)) {
      message.warning('Bệnh nhân đã có trong hàng đợi')
      return
    }
    setAppointments((prev) => prev.map((a) => (a.id === appt.id ? { ...a, status: 'đã check-in' } : a)))
    setQueue((prev) => [...prev, appt])
    message.success(`${appt.patient} đã vào hàng đợi khám`)
  }

  const callNext = () => {
    if (queue.length === 0) return
    const [called, ...rest] = queue
    setQueue(rest)
    // Sau khi khám xong -> hoàn tất -> tự chuyển vào lịch sử
    setAppointments((prev) => prev.map((a) => (a.id === called.id ? { ...a, status: 'hoàn tất' } : a)))
    message.info(`Đã gọi khám xong: ${called.patient} (${called.doctor}) — chuyển vào lịch sử`)
  }

  const activeColumns = [
    { title: 'Mã lịch', dataIndex: 'id', key: 'id' },
    { title: 'Bệnh nhân', dataIndex: 'patient', key: 'patient' },
    { title: 'Bác sĩ', dataIndex: 'doctor', key: 'doctor' },
    {
      title: 'Khung giờ',
      dataIndex: 'time',
      key: 'time',
      render: (t) => t.format('HH:mm DD/MM/YYYY'),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      render: (s) => <Tag color={statusColor[s]}>{s}</Tag>,
    },
    {
      title: '',
      key: 'actions',
      render: (_, record) => (
        <Space>
          <Button size="small" onClick={() => checkIn(record)}>Check-in</Button>
          <Button size="small" danger icon={<CloseCircleOutlined />} onClick={() => openCancel(record)}>Hủy</Button>
          <Button size="small" onClick={() => markNoShow(record)}>Không đến</Button>
        </Space>
      ),
    },
  ]

  const historyColumns = [
    { title: 'Mã lịch', dataIndex: 'id', key: 'id' },
    { title: 'Bệnh nhân', dataIndex: 'patient', key: 'patient' },
    { title: 'Bác sĩ', dataIndex: 'doctor', key: 'doctor' },
    { title: 'Khung giờ', dataIndex: 'time', key: 'time', render: (t) => t.format('HH:mm DD/MM/YYYY') },
    { title: 'Trạng thái', dataIndex: 'status', key: 'status', render: (s) => <Tag color={statusColor[s]}>{s}</Tag> },
    { title: 'Ghi chú', dataIndex: 'cancelReason', key: 'cancelReason', render: (v) => v || '—' },
  ]

  return (
    <div style={{ padding: 24 }}>
      <Space style={{ marginBottom: 16, width: '100%', justifyContent: 'space-between' }}>
        <h2 style={{ margin: 0 }}>Lịch hẹn khám</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={openBook}>Đặt lịch hẹn mới</Button>
      </Space>

      <Table rowKey="id" columns={activeColumns} dataSource={activeAppointments} pagination={{ pageSize: 8 }} />

      <Card
        title="Hàng đợi khám"
        style={{ marginTop: 24 }}
        extra={
          <Button icon={<UserSwitchOutlined />} onClick={callNext} disabled={queue.length === 0}>
            Gọi khám tiếp theo
          </Button>
        }
      >
        {queue.length === 0 ? (
          <Empty description="Hàng đợi trống" />
        ) : (
          <List
            dataSource={queue}
            renderItem={(item, index) => (
              <List.Item>
                <Space>
                  <Tag>{index + 1}</Tag>
                  {item.patient} — {item.doctor}
                </Space>
              </List.Item>
            )}
          />
        )}
      </Card>

      <Collapse
        style={{ marginTop: 24 }}
        items={[
          {
            key: 'history',
            label: `Lịch sử lịch hẹn (${historyAppointments.length})`,
            children: (
              <Table
                rowKey="id"
                columns={historyColumns}
                dataSource={historyAppointments}
                pagination={{ pageSize: 8 }}
                locale={{ emptyText: 'Chưa có lịch hẹn nào kết thúc' }}
              />
            ),
          },
        ]}
      />

      <Modal
        title="Đặt lịch hẹn mới"
        open={bookOpen}
        onCancel={() => setBookOpen(false)}
        onOk={() => bookForm.submit()}
        okText="Đặt lịch"
        cancelText="Hủy"
      >
        <Form form={bookForm} layout="vertical" onFinish={handleBook}>
          <Form.Item name="patient" label="Bệnh nhân" rules={[{ required: true, message: 'Chọn bệnh nhân' }]}>
            <Select
              showSearch
              placeholder="Chọn bệnh nhân"
              options={patientOptions}
              filterOption={(input, option) => option.label.toLowerCase().includes(input.toLowerCase())}
            />
          </Form.Item>
          <Form.Item name="doctor" label="Bác sĩ" rules={[{ required: true, message: 'Chọn bác sĩ' }]}>
            <Select
              showSearch
              placeholder="Chọn bác sĩ"
              options={doctorOptions}
              filterOption={(input, option) => option.label.toLowerCase().includes(input.toLowerCase())}
            />
          </Form.Item>
          <Form.Item name="time" label="Khung giờ" rules={[{ required: true, message: 'Chọn khung giờ' }]}>
            <DatePicker showTime format="HH:mm DD/MM/YYYY" style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`Hủy lịch hẹn — ${cancelingAppt?.id ?? ''}`}
        open={cancelOpen}
        onCancel={() => setCancelOpen(false)}
        onOk={() => cancelForm.submit()}
        okText="Xác nhận hủy"
        cancelText="Đóng"
      >
        <Form form={cancelForm} layout="vertical" onFinish={handleCancel}>
          <Form.Item name="reason" label="Lý do hủy" rules={[{ required: true, message: 'Nhập lý do hủy' }]}>
            <Input.TextArea rows={3} placeholder="Ví dụ: bệnh nhân yêu cầu đổi lịch" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default AppointmentQueue