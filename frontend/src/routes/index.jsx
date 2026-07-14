import { Routes, Route, Navigate } from 'react-router-dom'
import ProtectedRoute from '../components/ProtectedRoute'
import MainLayout from '../components/layout/MainLayout'
import LoginPage from '../pages/Login/LoginPage'
import DashboardPage from '../pages/Dashboard/DashboardPage'
import AccountManagementPage from '../pages/Admin/AccountManagementPage'
import ForbiddenPage from '../pages/NotFound/ForbiddenPage'
import NotFoundPage from '../pages/NotFound/NotFoundPage'
import { ROLES } from '../utils/constants'

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/403" element={<ForbiddenPage />} />

      {/* Mọi route bên dưới yêu cầu đã đăng nhập */}
      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>
          <Route path="/" element={<DashboardPage />} />

          {/* Chỉ ADMIN mới vào được quản lý tài khoản (NCL-01-CN-003) */}
          <Route element={<ProtectedRoute allowedRoles={[ROLES.ADMIN]} />}>
            <Route path="/admin/accounts" element={<AccountManagementPage />} />
          </Route>

          {/* Các module tiếp theo (NCL-02, NCL-03, ...) sẽ thêm route tại đây */}
        </Route>
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
