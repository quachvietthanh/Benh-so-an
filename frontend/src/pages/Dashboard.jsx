import React, { useState, useEffect } from 'react'
import { Card, Row, Col, Typography, Spin, List, Tag } from 'antd'
import {
  UserOutlined,
  FileTextOutlined,
  TeamOutlined,
  DollarCircleOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons'
import { useAuthContext } from '../context/AuthContext'
import { getDashboardStats, getAuditLogs } from '../services/mockDataService'

const { Title, Text } = Typography

function Dashboard() {
  const { user } = useAuthContext()
  const [loading, setLoading] = useState(true)
  const [stats, setStats] = useState(null)
  const [logs, setLogs] = useState([])

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = getDashboardStats()
        const audit = getAuditLogs()
        setStats(data)
        setLogs(audit)
      } catch (error) {
        console.error('Failed to fetch stats:', error)
      } finally {
        setLoading(false)
      }
    }
    fetchStats()
  }, [])

  if (loading || !stats) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', paddingTop: 100 }}>
        <Spin size="large" />
      </div>
    )
  }

  const statsConfig = [
    {
      key: 'totalPatients',
      icon: <TeamOutlined style={{ fontSize: 24, color: '#0ea5e9' }} />,
      label: 'Tổng bệnh nhân',
      value: stats.totalPatients,
      bg: 'linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%)',
    },
    {
      key: 'totalRecords',
      icon: <FileTextOutlined style={{ fontSize: 24, color: '#10b981' }} />,
      label: 'Hồ sơ bệnh án',
      value: stats.totalRecords,
      bg: 'linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%)',
    },
    {
      key: 'activeQueue',
      icon: <ClockCircleOutlined style={{ fontSize: 24, color: '#f59e0b' }} />,
      label: 'Đang chờ khám',
      value: stats.activeQueue,
      bg: 'linear-gradient(135deg, #fef3c7 0%, #fde68a 100%)',
    },
    {
      key: 'revenueToday',
      icon: <DollarCircleOutlined style={{ fontSize: 24, color: '#8b5cf6' }} />,
      label: 'Doanh thu hôm nay',
      value: new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(stats.revenueToday),
      bg: 'linear-gradient(135deg, #ede9fe 0%, #ddd6fe 100%)',
    },
  ]

  return (
    <div>
      <div className="page-header">
        <div>
          <Title className="page-title" level={3}>
            Xin chào, {user?.fullName || user?.username}! 👋
          </Title>
          <Text type="secondary">Tổng quan hoạt động phòng khám hôm nay.</Text>
        </div>
      </div>

      <Row gutter={[24, 24]} style={{ marginBottom: 24 }}>
        {statsConfig.map((stat) => (
          <Col xs={24} sm={12} lg={6} key={stat.key}>
            <Card 
              bodyStyle={{ padding: '24px' }}
              style={{ border: 'none', background: stat.bg }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                <div style={{ 
                  background: 'rgba(255,255,255,0.8)', 
                  padding: 16, 
                  borderRadius: '12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.05)'
                }}>
                  {stat.icon}
                </div>
                <div>
                  <Text type="secondary" style={{ color: 'rgba(0,0,0,0.6)', fontWeight: 500 }}>{stat.label}</Text>
                  <Title level={3} style={{ margin: 0, marginTop: 4, color: 'rgba(0,0,0,0.85)' }}>
                    {stat.value}
                  </Title>
                </div>
              </div>
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={[24, 24]}>
        <Col xs={24} lg={16}>
          <Card title={<span style={{ fontWeight: 600 }}>Hoạt động hệ thống</span>} className="animate-fade-in">
            <List
              itemLayout="horizontal"
              dataSource={logs}
              renderItem={item => (
                <List.Item>
                  <List.Item.Meta
                    avatar={<div style={{ width: 40, height: 40, borderRadius: '50%', background: '#e2e8f0', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><UserOutlined style={{ color: '#64748b' }} /></div>}
                    title={<span><Text strong>{item.user}</Text> đã {item.action.toLowerCase()}</span>}
                    description={`Bệnh nhân: ${item.patient} • ${item.time}`}
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card title={<span style={{ fontWeight: 600 }}>Bác sĩ đang trực</span>} className="animate-fade-in">
             <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 16 }}>
                <div style={{ width: 48, height: 48, borderRadius: '50%', background: '#0ea5e9', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold' }}>HA</div>
                <div>
                  <div style={{ fontWeight: 600 }}>BS. Phạm Hồng Anh</div>
                  <Tag color="green">Khoa Nội</Tag>
                </div>
             </div>
             <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <div style={{ width: 48, height: 48, borderRadius: '50%', background: '#10b981', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold' }}>NM</div>
                <div>
                  <div style={{ fontWeight: 600 }}>BS. Nguyễn Minh</div>
                  <Tag color="green">Khoa Nhi</Tag>
                </div>
             </div>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default Dashboard
