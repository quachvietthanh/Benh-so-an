import React, { useState, useMemo } from 'react'
import {
  Card, Row, Col, Table, Tag, Typography, Alert, Select, Button,
  Space, Statistic, Divider, DatePicker
} from 'antd'
import {
  BarChartOutlined, DownloadOutlined, UserOutlined, MedicineBoxOutlined,
  DollarCircleOutlined, CalendarOutlined, FileTextOutlined
} from '@ant-design/icons'
import { getAuditLogs, getInvoices, getMedicines, getRevenueByDay } from '../services/mockDataService'

const { Title, Text } = Typography

// ── Inline SVG bar chart ────────────────────────────────────────────────────
function RevenueBarChart({ data }) {
  if (!data || data.length === 0) return null
  const W = 600, H = 200, PAD = { top: 20, right: 20, bottom: 40, left: 60 }
  const chartW = W - PAD.left - PAD.right
  const chartH = H - PAD.top - PAD.bottom
  const maxVal = Math.max(...data.map((d) => d.revenue), 1)
  const barW = chartW / data.length - 8

  return (
    <svg viewBox={`0 0 ${W} ${H}`} style={{ width: '100%', height: 220 }}>
      {/* Y-axis gridlines */}
      {[0, 0.25, 0.5, 0.75, 1].map((frac, i) => {
        const y = PAD.top + chartH - frac * chartH
        const val = Math.round(maxVal * frac)
        return (
          <g key={i}>
            <line x1={PAD.left} y1={y} x2={W - PAD.right} y2={y} stroke="#e2e8f0" strokeWidth="1" />
            <text x={PAD.left - 6} y={y + 4} textAnchor="end" fontSize="10" fill="#94a3b8">
              {(val / 1000).toFixed(0)}k
            </text>
          </g>
        )
      })}
      {/* Bars */}
      {data.map((d, i) => {
        const barH = (d.revenue / maxVal) * chartH
        const x = PAD.left + i * (chartW / data.length) + 4
        const y = PAD.top + chartH - barH
        const label = d.date.slice(5) // MM-DD
        return (
          <g key={i}>
            <defs>
              <linearGradient id={`bar-grad-${i}`} x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#0ea5e9" />
                <stop offset="100%" stopColor="#6366f1" />
              </linearGradient>
            </defs>
            <rect x={x} y={y} width={barW} height={barH} rx="4" fill={`url(#bar-grad-${i})`} opacity="0.85" />
            <text x={x + barW / 2} y={y - 4} textAnchor="middle" fontSize="9" fill="#64748b">
              {(d.revenue / 1000).toFixed(0)}k
            </text>
            <text x={x + barW / 2} y={H - PAD.bottom + 14} textAnchor="middle" fontSize="10" fill="#64748b">
              {label}
            </text>
          </g>
        )
      })}
      {/* Axis lines */}
      <line x1={PAD.left} y1={PAD.top} x2={PAD.left} y2={PAD.top + chartH} stroke="#cbd5e1" strokeWidth="1.5" />
      <line x1={PAD.left} y1={PAD.top + chartH} x2={W - PAD.right} y2={PAD.top + chartH} stroke="#cbd5e1" strokeWidth="1.5" />
    </svg>
  )
}

// ── Visits bar chart ────────────────────────────────────────────────────────
function VisitsBarChart({ data }) {
  if (!data || data.length === 0) return null
  const W = 600, H = 160, PAD = { top: 20, right: 20, bottom: 40, left: 40 }
  const chartW = W - PAD.left - PAD.right
  const chartH = H - PAD.top - PAD.bottom
  const maxVal = Math.max(...data.map((d) => d.visits), 1)
  const barW = chartW / data.length - 8

  return (
    <svg viewBox={`0 0 ${W} ${H}`} style={{ width: '100%', height: 170 }}>
      {data.map((d, i) => {
        const barH = (d.visits / maxVal) * chartH
        const x = PAD.left + i * (chartW / data.length) + 4
        const y = PAD.top + chartH - barH
        return (
          <g key={i}>
            <rect x={x} y={y} width={barW} height={barH} rx="4" fill="#10b981" opacity="0.75" />
            <text x={x + barW / 2} y={y - 4} textAnchor="middle" fontSize="9" fill="#64748b">{d.visits}</text>
            <text x={x + barW / 2} y={H - PAD.bottom + 14} textAnchor="middle" fontSize="10" fill="#64748b">{d.date.slice(5)}</text>
          </g>
        )
      })}
      <line x1={PAD.left} y1={PAD.top + chartH} x2={W - PAD.right} y2={PAD.top + chartH} stroke="#cbd5e1" strokeWidth="1.5" />
    </svg>
  )
}

function ReportsPage() {
  const [auditLogs] = useState(getAuditLogs())
  const [invoices] = useState(getInvoices())
  const [medicines] = useState(getMedicines())
  const [revenueData] = useState(getRevenueByDay())
  const [filterUser, setFilterUser] = useState('')
  const [filterModule, setFilterModule] = useState('')

  const users = [...new Set(auditLogs.map((l) => l.user))]
  const modules = [...new Set(auditLogs.map((l) => l.module).filter(Boolean))]

  const filteredLogs = useMemo(() => {
    return auditLogs.filter((log) => {
      if (filterUser && log.user !== filterUser) return false
      if (filterModule && log.module !== filterModule) return false
      return true
    })
  }, [auditLogs, filterUser, filterModule])

  const totalRevenue = revenueData.reduce((s, d) => s + d.revenue, 0)
  const totalVisits = revenueData.reduce((s, d) => s + d.visits, 0)
  const paidInvoices = invoices.filter((inv) => inv.status === 'PAID').length
  const topMedicine = medicines.slice().sort((a, b) => b.stock - a.stock)[0]

  const handleExportCSV = () => {
    const header = 'Ngày,Doanh thu,Lượt khám\n'
    const rows = revenueData.map((d) => `${d.date},${d.revenue},${d.visits}`).join('\n')
    const blob = new Blob([header + rows], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'bao_cao_doanh_thu.csv'
    a.click()
    URL.revokeObjectURL(url)
  }

  const moduleLabel = {
    'medical-records': 'Bệnh án',
    'patients': 'Bệnh nhân',
    'prescriptions': 'Kê đơn',
    'pharmacy': 'Kho thuốc',
    'billing': 'Hóa đơn',
    'users': 'Hệ thống',
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <Title className="page-title" level={3}>Báo cáo &amp; Thống kê vận hành</Title>
          <Text type="secondary">Doanh thu, lượt khám, nhật ký truy cập bệnh án (NCL-08)</Text>
        </div>
        <Button type="primary" icon={<DownloadOutlined />} size="large" onClick={handleExportCSV}>
          Xuất CSV
        </Button>
      </div>

      <Alert type="success" showIcon message="Báo cáo theo thời gian thực. Nhật ký truy cập bệnh án được ghi đầy đủ, có thể lọc theo người dùng và phân hệ." style={{ marginBottom: 16 }} />

      {/* KPI cards */}
      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card style={{ borderRadius: 12, background: 'linear-gradient(135deg,#e0f2fe,#bae6fd)', border: 'none' }}>
            <Statistic
              title={<Text style={{ color: '#0369a1' }}>Doanh thu 7 ngày</Text>}
              value={totalRevenue}
              suffix="₫"
              formatter={(v) => v.toLocaleString('vi-VN')}
              valueStyle={{ color: '#0369a1', fontWeight: 700 }}
              prefix={<DollarCircleOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card style={{ borderRadius: 12, background: 'linear-gradient(135deg,#d1fae5,#a7f3d0)', border: 'none' }}>
            <Statistic
              title={<Text style={{ color: '#065f46' }}>Tổng lượt khám</Text>}
              value={totalVisits}
              suffix="lượt"
              valueStyle={{ color: '#065f46', fontWeight: 700 }}
              prefix={<CalendarOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card style={{ borderRadius: 12, background: 'linear-gradient(135deg,#fef3c7,#fde68a)', border: 'none' }}>
            <Statistic
              title={<Text style={{ color: '#92400e' }}>Hóa đơn đã thanh toán</Text>}
              value={paidInvoices}
              suffix={`/ ${invoices.length}`}
              valueStyle={{ color: '#92400e', fontWeight: 700 }}
              prefix={<FileTextOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card style={{ borderRadius: 12, background: 'linear-gradient(135deg,#ede9fe,#ddd6fe)', border: 'none' }}>
            <Statistic
              title={<Text style={{ color: '#5b21b6' }}>Thuốc tồn kho nhiều nhất</Text>}
              value={topMedicine?.name || '—'}
              valueStyle={{ color: '#5b21b6', fontSize: 14, fontWeight: 700 }}
              prefix={<MedicineBoxOutlined />}
            />
          </Card>
        </Col>
      </Row>

      {/* Charts */}
      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={24} lg={16}>
          <Card
            title={<Space><BarChartOutlined style={{ color: '#0ea5e9' }} /><span>Biểu đồ doanh thu 7 ngày (nghìn ₫)</span></Space>}
            style={{ borderRadius: 12 }}
          >
            <RevenueBarChart data={revenueData} />
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card
            title={<Space><CalendarOutlined style={{ color: '#10b981' }} /><span>Lượt khám 7 ngày</span></Space>}
            style={{ borderRadius: 12 }}
          >
            <VisitsBarChart data={revenueData} />
          </Card>
        </Col>
      </Row>

      {/* Audit log with filters */}
      <Card
        title={<Space><UserOutlined style={{ color: '#6366f1' }} /><span>Nhật ký truy cập bệnh án</span></Space>}
        style={{ borderRadius: 12, marginBottom: 16 }}
        extra={
          <Space>
            <Select
              style={{ width: 160 }}
              placeholder="Lọc theo người dùng"
              allowClear
              onChange={(v) => setFilterUser(v || '')}
            >
              {users.map((u) => <Select.Option key={u} value={u}>{u}</Select.Option>)}
            </Select>
            <Select
              style={{ width: 150 }}
              placeholder="Lọc theo phân hệ"
              allowClear
              onChange={(v) => setFilterModule(v || '')}
            >
              {modules.map((m) => (
                <Select.Option key={m} value={m}>{moduleLabel[m] || m}</Select.Option>
              ))}
            </Select>
          </Space>
        }
      >
        <Table
          columns={[
            { title: 'Người dùng', dataIndex: 'user', key: 'user', render: (u) => <Text strong>{u}</Text> },
            { title: 'Bệnh nhân', dataIndex: 'patient', key: 'patient' },
            { title: 'Hành động', dataIndex: 'action', key: 'action' },
            {
              title: 'Phân hệ',
              dataIndex: 'module',
              key: 'module',
              render: (m) => m ? <Tag color="geekblue">{moduleLabel[m] || m}</Tag> : '—',
            },
            { title: 'Thời gian', dataIndex: 'time', key: 'time' },
          ]}
          dataSource={filteredLogs}
          rowKey="id"
          pagination={{ pageSize: 8 }}
        />
      </Card>

      {/* Invoice summary */}
      <Card
        title={<Space><DollarCircleOutlined style={{ color: '#10b981' }} /><span>Tóm tắt hóa đơn</span></Space>}
        style={{ borderRadius: 12 }}
      >
        <Table
          columns={[
            { title: 'Mã hóa đơn', dataIndex: 'invoiceCode', key: 'invoiceCode' },
            { title: 'Bệnh nhân', dataIndex: 'patientName', key: 'patientName' },
            { title: 'Số tiền', dataIndex: 'amount', key: 'amount', render: (a) => `${a.toLocaleString('vi-VN')} ₫` },
            {
              title: 'Trạng thái', dataIndex: 'status', key: 'status',
              render: (s) => <Tag color={s === 'PAID' ? 'green' : 'orange'}>{s === 'PAID' ? 'Đã TT' : 'Chờ TT'}</Tag>,
            },
            {
              title: 'Điều chỉnh', dataIndex: 'adjustmentOf', key: 'adjustmentOf',
              render: (adj) => adj ? <Tag color="purple">Có</Tag> : '—',
            },
          ]}
          dataSource={invoices}
          rowKey="id"
          pagination={{ pageSize: 5 }}
        />
      </Card>
    </div>
  )
}

export default ReportsPage
