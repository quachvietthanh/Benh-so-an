import React from 'react'
import { BrowserRouter } from 'react-router-dom'
import { ConfigProvider } from 'antd'
import AppRoutes from './routes/index.jsx' // Nếu trên GitHub file là routes/index.jsx thì giữ dòng này
import './App.css'

function App() {
  return (
    <BrowserRouter>
      <ConfigProvider
        theme={{
          token: {
            colorPrimary: '#0ea5e9',
            fontFamily:
              "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif",
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
            },
          },
        }}
      >
        <AppRoutes />
      </ConfigProvider>
    </BrowserRouter>
  )
}

export default App