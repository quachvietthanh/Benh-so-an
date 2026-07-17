import React from 'react'
import { Navigate } from 'react-router-dom'
import { useAuthContext } from '../../context/AuthContext'

/**
 * RoleRoute — Component bảo vệ route dựa trên quyền.
 *
 * Cách dùng:
 *   <Route path="/admin/users" element={
 *     <RoleRoute roles={['ADMIN']}>
 *       <UserList />
 *     </RoleRoute>
 *   } />
 */
function RoleRoute({ roles, redirectTo = '/', children }) {
  const { user } = useAuthContext()

  // Chưa đăng nhập → về trang login
  if (!user) {
    return <Navigate to="/login" replace />
  }

  // Không có quyền → chuyển hướng
  const hasAccess = roles.some((role) => user.roles?.includes(role))
  if (!hasAccess) {
    return <Navigate to={redirectTo} replace />
  }

  return <>{children}</>
}

export default RoleRoute
