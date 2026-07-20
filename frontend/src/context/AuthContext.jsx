import React, { createContext, useState, useContext, useEffect } from 'react'
import authApi from '../api/authApi'

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
      const response = await authApi.login(credentials)
      const data = response.data

      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify({
        username: data.username,
        fullName: data.fullName,
        email: data.email,
        roles: data.roles,
      }))

      setUser({
        username: data.username,
        fullName: data.fullName,
        email: data.email,
        roles: data.roles,
      })

      return { success: true }
    } catch (error) {
      const message = error.response?.data?.message || 'Đăng nhập thất bại'
      return { success: false, message }
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

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
