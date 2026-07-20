import React, { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { Table, Button, Input, Space, Tag, Typography, Spin } from 'antd'
import { PlusOutlined, SearchOutlined, EyeOutlined } from '@ant-design/icons'
import patientApi from '../api/patientApi'
import { formatDate, formatGender } from '../utils/helpers'

const { Title } = Typography

function PatientList() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [patients, setPatients] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(10)
  const [keyword, setKeyword] = useState('')
  const [searchText, setSearchText] = useState('')

  const fetchPatients = useCallback(async () => {
    setLoading(true)
    try {
      const params = {
        page,
        size: pageSize,
        ...(keyword && { keyword }),
      }
      const response = await patientApi.getAll(params)
      setPatients(response.data.content)
      setTotal(response.data.totalElements)
    } catch (error) {
      console.error('Failed to fetch patients:', error)
    } finally {
      setLoading(false)
    }
  }, [page, pageSize, keyword])

  useEffect(() => {
    fetchPatients()
  }, [fetchPatients])

  const handleSearch = () => {
    setPage(0)
    setKeyword(searchText)
  }

  const columns = [
    {
      title: 'Mã BN',
      dataIndex: 'patientCode',
      key: 'patientCode',
      width: 120,
      render: (text) => <Tag color="blue">{text}</Tag>,
    },
    {
      title: 'Họ tên',
      dataIndex: 'fullName',
      key: 'fullName',
      ellipsis: true,
    },
    {
      title: 'Ngày sinh',
      dataIndex: 'dateOfBirth',
      key: 'dateOfBirth',
      width: 120,
      render: (date) => formatDate(date),
    },
    {
      title: 'Giới tính',
      dataIndex: 'gender',
      key: 'gender',
      width: 100,
      render: (gender) => formatGender(gender),
    },
    {
      title: 'Số điện thoại',
      dataIndex: 'phoneNumber',
      key: 'phoneNumber',
      width: 140,
    },
    {
      title: 'BHYT',
      dataIndex: 'healthInsuranceCode',
      key: 'healthInsuranceCode',
      width: 140,
      ellipsis: true,
    },
    {
      title: 'Thao tác',
      key: 'actions',
      width: 100,
      render: (_, record) => (
        <Button
          type="link"
          icon={<EyeOutlined />}
          onClick={() => navigate(`/patients/${record.id}`)}
        >
          Xem
        </Button>
      ),
    },
  ]

  return (
    <div>
      <div className="page-header">
        <Title level={4} style={{ margin: 0 }}>Quản lý bệnh nhân</Title>
        <Space>
          <Input.Search
            placeholder="Tìm kiếm bệnh nhân..."
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onSearch={handleSearch}
            enterButton
            style={{ width: 300 }}
          />
          <Button type="primary" icon={<PlusOutlined />}>
            Thêm bệnh nhân
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={patients}
        rowKey="id"
        loading={loading}
        pagination={{
          current: page + 1,
          pageSize,
          total,
          showSizeChanger: true,
          showTotal: (total) => `Tổng số: ${total} bệnh nhân`,
          onChange: (newPage, newSize) => {
            setPage(newPage - 1)
            setPageSize(newSize)
          },
        }}
      />
    </div>
  )
}

export default PatientList
