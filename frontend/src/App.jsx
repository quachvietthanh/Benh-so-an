import React from 'react'
import AppRoutes from './routes/AppRoutes'
import './App.css'
import { ConfigProvider } from 'antd'

function App() {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#0ea5e9',
          fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif",
          borderRadius: 8,
          colorBgContainer: '#ffffff',
          colorText: '#1e293b',
          colorTextSecondary: '#64748b',
          colorBorder: '#e2e8f0',
          controlHeight: 36,
        },
        components: {
          Card: {
            paddingLG: 20,
          }
        }
      }}
    >
      <AppRoutes />
    </ConfigProvider>
  )
}

export default App
