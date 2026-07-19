import React, { createContext, useState, useContext, useEffect } from 'react'
import { loginUser } from '../services/mockDataService'

const AuthContext = createContext(null)

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const storedUser = localStorage.getItem('user')
    const storedToken = localStorage.getItem('token')
    if (storedUser && storedToken) {
      setUser(JSON.parse(storedUser))
    }
    setLoading(false)
  }, [])

  const login = async (credentials) => {
    try {
      const data = loginUser(credentials)

      const normalizedUser = {
        id: data.id,
        username: data.username,
        fullName: data.fullName,
        email: data.email,
        roles: data.roles,
      }

      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(normalizedUser))
      setUser(normalizedUser)

      return { success: true }
    } catch (error) {
      return { success: false, message: error.message || 'Đăng nhập thất bại' }
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }
// TC-03: không thao tác gì quá 15 phút -> tự động hết phiên
  useEffect(() => {
    if (!user) return undefined
    const TIMEOUT = 15 * 60 * 1000
    let timer = setTimeout(logout, TIMEOUT)
    const resetTimer = () => {
      clearTimeout(timer)
      timer = setTimeout(logout, TIMEOUT)
    }
    const events = ['mousedown', 'keydown', 'scroll']
    events.forEach((e) => window.addEventListener(e, resetTimer))
    return () => {
      clearTimeout(timer)
      events.forEach((e) => window.removeEventListener(e, resetTimer))
    }
  }, [user])
  const isAuthenticated = !!user

  return (
    <AuthContext.Provider value={{ user, login, logout, loading, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuthContext = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuthContext must be used within an AuthProvider')
  }
  return context
}

export default AuthContext
