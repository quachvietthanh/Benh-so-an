import React, { useState, useEffect } from 'react'
import { Card, Row, Col, Typography, Spin } from 'antd'
import {
  UserOutlined,
  FileTextOutlined,
  TeamOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons'
import { useAuthContext } from '../context/AuthContext'

const { Title } = Typography

const statsConfig = [
  {
    key: 'totalPatients',
    icon: <TeamOutlined style={{ color: '#1890ff' }} />,
    label: 'Tổng bệnh nhân',
    color: '#e6f7ff',
  },
  {
    key: 'totalRecords',
    icon: <FileTextOutlined style={{ color: '#52c41a' }} />,
    label: 'Tổng hồ sơ bệnh án',
    color: '#f6ffed',
  },
  {
    key: 'activeRecords',
    icon: <CheckCircleOutlined style={{ color: '#faad14' }} />,
    label: 'Đang điều trị',
    color: '#fffbe6',
  },
  {
    key: 'totalDoctors',
    icon: <UserOutlined style={{ color: '#722ed1' }} />,
    label: 'Bác sĩ',
    color: '#f9f0ff',
  },
]

function Dashboard() {
  const { user } = useAuthContext()
  const [loading, setLoading] = useState(true)
  const [stats, setStats] = useState({
    totalPatients: 0,
    totalRecords: 0,
    activeRecords: 0,
    totalDoctors: 0,
  })

  useEffect(() => {
    // TODO: Gọi API để lấy thống kê
    const fetchStats = async () => {
      try {
        // const response = await dashboardApi.getStats()
        // setStats(response.data)
        setStats({
          totalPatients: 1250,
          totalRecords: 3450,
          activeRecords: 180,
          totalDoctors: 25,
        })
      } catch (error) {
        console.error('Failed to fetch stats:', error)
      } finally {
        setLoading(false)
      }
    }

    fetchStats()
  }, [])

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', paddingTop: 100 }}>
        <Spin size="large" />
      </div>
    )
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <Title level={4} style={{ margin: 0 }}>
            Xin chào, {user?.fullName || user?.username}!
          </Title>
          <p style={{ color: '#666', marginTop: 4 }}>Chào mừng đến với hệ thống quản lý bệnh án</p>
        </div>
      </div>

      <div className="dashboard-stats">
        {statsConfig.map((stat) => (
          <div
            key={stat.key}
            className="stat-card"
            style={{ borderLeft: `4px solid ${stat.color.replace('f', 'f')}` }}
          >
            <div className="stat-icon">{stat.icon}</div>
            <div className="stat-value">{stats[stat.key].toLocaleString()}</div>
            <div className="stat-label">{stat.label}</div>
          </div>
        ))}
      </div>

      <Row gutter={24}>
        <Col span={12}>
          <Card title="Hoạt động gần đây" style={{ borderRadius: 12 }}>
            <p style={{ color: '#999', textAlign: 'center', padding: '40px 0' }}>
              Chưa có dữ liệu hoạt động
            </p>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="Thông báo" style={{ borderRadius: 12 }}>
            <p style={{ color: '#999', textAlign: 'center', padding: '40px 0' }}>
              Chưa có thông báo
            </p>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default Dashboard
