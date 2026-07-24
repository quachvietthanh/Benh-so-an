import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api(\/v1)?/, '/api/v1'),
        configure: (proxy) => {
          proxy.on('error', (err, _req, _res) => {
            console.error('Vite Proxy Error:', err)
          })
        }
      }
    }
  }
})
