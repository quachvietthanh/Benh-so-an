import { useState } from 'react'
import authApi from '../api/authApi'

export const useAuth = () => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const login = async (credentials) => {
    setLoading(true)
    setError(null)
    try {
      const response = await authApi.login(credentials)
      const data = response.data

      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify({
        username: data.username,
        fullName: data.fullName,
        email: data.email,
        roles: data.roles,
      }))

      return { success: true, data }
    } catch (err) {
      const message = err.response?.data?.message || 'Đăng nhập thất bại'
      setError(message)
      return { success: false, message }
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.href = '/login'
  }

  return { login, logout, loading, error }
}
