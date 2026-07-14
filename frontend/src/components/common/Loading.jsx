import React from 'react'
import { Spin } from 'antd'

function Loading({ tip = 'Đang tải...', fullPage = false }) {
  if (fullPage) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
      }}>
        <Spin size="large" tip={tip} />
      </div>
    )
  }

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      padding: '40px 0',
    }}>
      <Spin size="large" tip={tip} />
    </div>
  )
}

export default Loading
