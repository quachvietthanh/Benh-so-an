import axios from 'axios'

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Gắn token vào mọi request
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// NCL-01-CN-001-TC-03: phiên làm việc quá hạn -> bắt đăng nhập lại
axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('currentUser')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login?expired=1'
      }
    }
    return Promise.reject(error)
  },
)

export default axiosClient
