import React from 'react'
import { Pagination as AntPagination } from 'antd'

function Pagination({ current, total, pageSize, onChange, showSizeChanger = true }) {
  return (
    <div style={{
      display: 'flex',
      justifyContent: 'flex-end',
      padding: '16px 0',
    }}>
      <AntPagination
        current={current + 1}
        total={total}
        pageSize={pageSize}
        showSizeChanger={showSizeChanger}
        showTotal={(total) => `Tổng số: ${total}`}
        onChange={(page, size) => onChange(page - 1, size)}
      />
    </div>
  )
}

export default Pagination
