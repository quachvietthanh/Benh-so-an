import React, { useCallback, useEffect, useState } from 'react'
import { Button, Card, Collapse, DatePicker, Empty, Form, Input, List, message, Modal, Select, Space, Table, Tag } from 'antd'
import { BellOutlined, CloseCircleOutlined, PlusOutlined, UserSwitchOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import appointmentApi from '../api/appointmentApi'
import patientApi from '../api/patientApi'
import { useAuthContext } from '../context/AuthContext'

const ended = ['CANCELLED', 'NO_SHOW', 'COMPLETED']
const labels = { SCHEDULED: 'Đã đặt', CHECKED_IN: 'Đã check-in', CALLED: 'Đang gọi khám', COMPLETED: 'Hoàn tất', CANCELLED: 'Đã hủy', NO_SHOW: 'Không đến' }
const colors = { SCHEDULED: 'blue', CHECKED_IN: 'green', CALLED: 'orange', COMPLETED: 'purple', CANCELLED: 'default', NO_SHOW: 'red' }

function AppointmentQueue() {
  const { user } = useAuthContext()
  const canManage = user?.roles?.some((role) => ['admin', 'receptionist'].includes(role))
  const [appointments, setAppointments] = useState([])
  const [queue, setQueue] = useState([])
  const [patients, setPatients] = useState([])
  const [doctors, setDoctors] = useState([])
  const [loading, setLoading] = useState(false)
  const [bookOpen, setBookOpen] = useState(false)
  const [cancelItem, setCancelItem] = useState(null)
  const [bookForm] = Form.useForm()
  const [cancelForm] = Form.useForm()

  const loadData = useCallback(async () => {
    setLoading(true)
    try {
      const [appointmentRes, queueRes, patientRes, doctorRes] = await Promise.all([
        appointmentApi.getAll(), appointmentApi.getQueue(), patientApi.getAll({ page: 0, size: 200 }), appointmentApi.getDoctors(),
      ])
      setAppointments(appointmentRes.data)
      setQueue(queueRes.data)
      setPatients(patientRes.data.content || [])
      setDoctors(doctorRes.data)
    } catch (error) {
      message.error(error.response?.data?.message || 'Không thể tải lịch hẹn')
    } finally { setLoading(false) }
  }, [])

  useEffect(() => { loadData() }, [loadData])

  const runAction = async (action, successMessage) => {
    try { await action(); message.success(successMessage); await loadData() }
    catch (error) { message.error(error.response?.data?.message || 'Không thể thực hiện thao tác') }
  }

  const createAppointment = (values) => runAction(async () => {
    await appointmentApi.create({ ...values, appointmentAt: values.appointmentAt.toISOString() })
    setBookOpen(false); bookForm.resetFields()
  }, 'Đặt lịch hẹn thành công')

  const cancelAppointment = (values) => runAction(async () => {
    await appointmentApi.cancel(cancelItem.id, values.reason)
    setCancelItem(null); cancelForm.resetFields()
  }, 'Đã hủy lịch hẹn và giải phóng khung giờ')

  const active = appointments.filter((item) => !ended.includes(item.status))
  const history = appointments.filter((item) => ended.includes(item.status))

  const columns = [
    { title: 'Mã lịch', dataIndex: 'appointmentCode' },
    { title: 'Bệnh nhân', dataIndex: 'patientName' },
    { title: 'Bác sĩ', dataIndex: 'doctorName' },
    { title: 'Chuyên khoa', dataIndex: 'department' },
    { title: 'Thời gian', dataIndex: 'appointmentAt', render: (value) => dayjs(value).format('HH:mm DD/MM/YYYY') },
    { title: 'Trạng thái', dataIndex: 'status', render: (value, item) => <Space><Tag color={colors[value]}>{labels[value]}</Tag>{item.reminderSentAt && <Tag icon={<BellOutlined />} color="cyan">Đã nhắc</Tag>}</Space> },
    ...(canManage ? [{ title: 'Thao tác', render: (_, item) => <Space wrap>
      {item.status === 'SCHEDULED' && <><Button size="small" onClick={() => runAction(() => appointmentApi.checkIn(item.id), 'Đã đưa bệnh nhân vào hàng đợi')}>Check-in</Button><Button size="small" danger icon={<CloseCircleOutlined />} onClick={() => setCancelItem(item)}>Hủy</Button><Button size="small" onClick={() => runAction(() => appointmentApi.noShow(item.id), 'Đã ghi nhận bệnh nhân không đến')}>Không đến</Button></>}
      {item.status === 'CALLED' && <Button size="small" type="primary" onClick={() => runAction(() => appointmentApi.complete(item.id), 'Đã hoàn tất lượt khám')}>Hoàn tất</Button>}
    </Space> }] : []),
  ]

  return <div style={{ padding: 24 }}>
    <Space style={{ marginBottom: 16, width: '100%', justifyContent: 'space-between' }}><h2 style={{ margin: 0 }}>Lịch hẹn và hàng đợi khám</h2>{canManage && <Button type="primary" icon={<PlusOutlined />} onClick={() => setBookOpen(true)}>Đặt lịch hẹn</Button>}</Space>
    <Table rowKey="id" columns={columns} dataSource={active} loading={loading} pagination={{ pageSize: 10 }} />
    <Card title="Hàng đợi khám" style={{ marginTop: 24 }} extra={canManage && <Button icon={<UserSwitchOutlined />} disabled={!queue.length} onClick={() => runAction(appointmentApi.callNext, 'Đã gọi bệnh nhân tiếp theo')}>Gọi khám tiếp theo</Button>}>
      {!queue.length ? <Empty description="Hàng đợi trống" /> : <List dataSource={queue} renderItem={(item, index) => <List.Item><Space><Tag color="blue">{index + 1}</Tag><strong>{item.patientName}</strong><span>— {item.doctorName}</span><span>— check-in {dayjs(item.checkedInAt).format('HH:mm')}</span></Space></List.Item>} />}
    </Card>
    <Collapse style={{ marginTop: 24 }} items={[{ key: 'history', label: `Lịch sử (${history.length})`, children: <Table rowKey="id" columns={columns.filter((column) => column.title !== 'Thao tác')} dataSource={history} pagination={{ pageSize: 10 }} /> }]} />

    <Modal title="Đặt lịch hẹn khám" open={bookOpen} onCancel={() => setBookOpen(false)} onOk={() => bookForm.submit()} okText="Đặt lịch" cancelText="Hủy">
      <Form form={bookForm} layout="vertical" onFinish={createAppointment}>
        <Form.Item name="patientId" label="Bệnh nhân" rules={[{ required: true }]}><Select showSearch optionFilterProp="label" options={patients.map((p) => ({ value: p.id, label: `${p.fullName} (${p.patientCode})` }))} /></Form.Item>
        <Form.Item name="department" label="Chuyên khoa/phòng khám" rules={[{ required: true }]}><Select options={['Nội tổng quát', 'Ngoại khoa', 'Nhi khoa', 'Sản phụ khoa', 'Tim mạch', 'Tai Mũi Họng'].map((value) => ({ value, label: value }))} /></Form.Item>
        <Form.Item name="doctorId" label="Bác sĩ" rules={[{ required: true }]}><Select showSearch optionFilterProp="label" options={doctors.map((d) => ({ value: d.id, label: d.fullName }))} /></Form.Item>
        <Form.Item name="appointmentAt" label="Ngày giờ khám" rules={[{ required: true }]}><DatePicker showTime minuteStep={15} format="HH:mm DD/MM/YYYY" style={{ width: '100%' }} disabledDate={(date) => date && date.endOf('day').isBefore(dayjs())} /></Form.Item>
        <Form.Item name="reason" label="Lý do khám"><Input.TextArea rows={2} /></Form.Item>
      </Form>
    </Modal>
    <Modal title={`Hủy lịch ${cancelItem?.appointmentCode || ''}`} open={!!cancelItem} onCancel={() => setCancelItem(null)} onOk={() => cancelForm.submit()} okText="Xác nhận hủy" cancelText="Đóng">
      <Form form={cancelForm} layout="vertical" onFinish={cancelAppointment}><Form.Item name="reason" label="Lý do hủy" rules={[{ required: true, message: 'Vui lòng nhập lý do hủy' }]}><Input.TextArea rows={3} /></Form.Item></Form>
    </Modal>
  </div>
}

export default AppointmentQueue
