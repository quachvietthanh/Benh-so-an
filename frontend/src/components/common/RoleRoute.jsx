import React from 'react'
import { Navigate } from 'react-router-dom'
import { useAuthContext } from '../../context/AuthContext'

/**
 * RoleRoute - Route guard kiểm tra quyền truy cập trang
 * 
 * Usage:
 * <Route path="/admin" element={
 *   <RoleRoute roles={['ADMIN']} fallback="/">
 *     <AdminPage />
 *   </RoleRoute>
 * } />
 */
const RoleRoute = ({ 
  children, 
  roles = [], 
  fallback = '/',
  permissions = []
}) => {
  const { user, isAuthenticated, loading } = useAuthContext()

  if (loading) {
    return <div>Đang tải...</div>
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  const userRoles = user?.roles || []
  const userPermissions = user?.permissions || []

  // Check roles
  if (roles.length > 0) {
    const hasRole = roles.some(role => userRoles.includes(role))
    if (!hasRole) {
      return <Navigate to={fallback} replace />
    }
  }

  // Check permissions
  if (permissions.length > 0) {
    const hasPermission = permissions.some(p => userPermissions.includes(p))
    if (!hasPermission) {
      return <Navigate to={fallback} replace />
    }
  }

  return children
}

export default RoleRoute
