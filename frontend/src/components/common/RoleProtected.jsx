import React from 'react'
import { useAuthContext } from '../../context/AuthContext'

/**
 * RoleProtected - Component bảo vệ hiển thị dựa trên role của user
 * 
 * Usage:
 * <RoleProtected roles={['ADMIN', 'DOCTOR']}>
 *   <Button>Chỉ Admin và Doctor mới thấy</Button>
 * </RoleProtected>
 * 
 * <RoleProtected roles={['ADMIN']} fallback={<p>Bạn không có quyền</p>}>
 *   <Button>Admin-only content</Button>
 * </RoleProtected>
 */
const RoleProtected = ({ 
  children, 
  roles = [], 
  fallback = null,
  permissions = [] 
}) => {
  const { user } = useAuthContext()

  if (!user) {
    return fallback
  }

  // Check by roles
  if (roles.length > 0) {
    const userRoles = user.roles || []
    const hasRole = roles.some(role => userRoles.includes(role))
    if (hasRole) return children
  }

  // Check by permissions
  if (permissions.length > 0) {
    const userPermissions = user.permissions || []
    const hasPermission = permissions.some(p => userPermissions.includes(p))
    if (hasPermission) return children
  }

  // If no specific role/permission required, show to all authenticated
  if (roles.length === 0 && permissions.length === 0) {
    return children
  }

  return fallback
}

export default RoleProtected
