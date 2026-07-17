import React from 'react'
import { useAuthContext } from '../../context/AuthContext'

/**
 * RoleProtected — Component bọc ngoài để ẩn/hiện UI dựa trên quyền.
 *
 * Cách dùng:
 *   <RoleProtected roles={['ADMIN', 'DOCTOR']}>
 *     <Button>Xóa bệnh nhân</Button>
 *   </RoleProtected>
 *
 *   // Fallback khi không có quyền
 *   <RoleProtected roles={['ADMIN']} fallback={<span>Không có quyền</span>}>
 *     <Button>Quản lý người dùng</Button>
 *   </RoleProtected>
 */
function RoleProtected({ roles, fallback = null, children }) {
  const { user } = useAuthContext()

  // Nếu chưa đăng nhập → ẩn
  if (!user || !user.roles) {
    return fallback
  }

  // Kiểm tra user có ít nhất 1 role trong danh sách được phép
  const hasAccess = roles.some((role) => user.roles.includes(role))

  if (!hasAccess) {
    return fallback
  }

  return <>{children}</>
}

export default RoleProtected
