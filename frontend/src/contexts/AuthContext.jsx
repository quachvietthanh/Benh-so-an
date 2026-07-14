import { createContext, useContext, useEffect, useState } from 'react'
import authApi from '../api/authApi'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('currentUser')
    return saved ? JSON.parse(saved) : null
  })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // Khôi phục phiên nếu còn token hợp lệ
    const token = localStorage.getItem('accessToken')
    if (!token) {
      setLoading(false)
      return
    }
    authApi
      .getCurrentUser()
      .then((res) => setUser(res.data))
      .catch(() => {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('currentUser')
        setUser(null)
      })
      .finally(() => setLoading(false))
  }, [])

  // NCL-01-CN-001-TC-01/02: đăng nhập, hệ thống trả vai trò để điều hướng đúng trang
  const login = async (username, password) => {
    const res = await authApi.login(username, password)
    const { accessToken, user: loggedInUser } = res.data
    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('currentUser', JSON.stringify(loggedInUser))
    setUser(loggedInUser)
    return loggedInUser
  }

  const logout = async () => {
    try {
      await authApi.logout()
    } finally {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('currentUser')
      setUser(null)
    }
  }

  const hasRole = (...roles) => !!user && roles.includes(user.role)

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, hasRole }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
